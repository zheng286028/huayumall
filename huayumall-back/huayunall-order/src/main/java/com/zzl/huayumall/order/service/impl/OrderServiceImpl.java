package com.zzl.huayumall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zzl.common.to.mq.OrderTo;
import com.zzl.common.utils.R;
import com.zzl.common.vo.UserItemVo;
import com.zzl.huayumall.order.constant.OrderConstant;
import com.zzl.huayumall.order.entity.OrderItemEntity;
import com.zzl.huayumall.order.entity.PaymentInfoEntity;
import com.zzl.huayumall.order.enume.OrderStatusEnum;
import com.zzl.huayumall.order.feign.CartFeignService;
import com.zzl.huayumall.order.feign.MemberFeignService;
import com.zzl.huayumall.order.feign.ProductFeignService;
import com.zzl.huayumall.order.feign.WareFeignService;
import com.zzl.huayumall.order.interceptor.UserLongInterceptor;
import com.zzl.huayumall.order.service.OrderItemService;
import com.zzl.huayumall.order.service.PaymentInfoService;
import com.zzl.huayumall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.order.dao.OrderDao;
import com.zzl.huayumall.order.entity.OrderEntity;
import com.zzl.huayumall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;

/**
 * @Author 郑子朗
 * @Description
 * @Date
 * @Param
 * @return
 */
@Service("orderService")
@Slf4j
@RabbitListener(queues = {"java-queue"})
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    public static final ThreadLocal<SubmitOrderVo> orderVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderItemService orderItemService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 封装订单确认页所需的数据
     *
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo order = new OrderConfirmVo();
        //1、远程查询MemberAddress服务查询地址信息
        //1.1、走到这里该用户已经登录，无需判断
        UserItemVo userItemVo = UserLongInterceptor.thread.get();
        //1.2、Request请求的共享数据是存储在当前线程的，以下开启了异步线程，就是不同线程，无法获取到当前线程的request请求数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> memberUserAddress = CompletableFuture.runAsync(() -> {
            //给当前线程设置请求头
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> memberAddress = memberFeignService.getMemberAddress(userItemVo.getId());
            if (memberAddress != null && memberAddress.size() > 0) {
                order.setAddress(memberAddress);
            }
        }, executor);

        CompletableFuture<Void> cartAttrValue = CompletableFuture.runAsync(() -> {
            //2、远程查询购物车的购物项
            //给当前线程设置请求头
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> userCartItem = cartFeignService.getUserCartItem();
            if (userCartItem != null && userCartItem.size() > 0) {
                order.setItems(userCartItem);
            }
        }, executor).thenRunAsync(() -> {
            //3、收集当前商品id
            List<Long> collect = order.getItems().stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            //3.1、查询库存
            List<SkuStockVo> skuStockVos = wareFeignService.selectWareHasSkuStock(collect);
            if (skuStockVos != null && skuStockVos.size() > 0) {
                Map<Long, Boolean> stockMap = skuStockVos.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getStock));
                order.setStock(stockMap);
            }
        }, executor);
        //4、优惠积分
        order.setIntegration(userItemVo.getIntegration());
        CompletableFuture.allOf(memberUserAddress, cartAttrValue).get();
        //5、创建防重令牌
        String token = UUID.randomUUID().toString();
        order.setToken(token);
        //6、token在redis也要存储一份key为order:token:用户id
        stringRedisTemplate.opsForValue().set(OrderConstant.ORDER_TOKEN_USER_PREFIX + userItemVo.getId(), token);
        return order;
    }

    /**
     * 提交订单
     *
     * @param vo
     * @return
     */
    @Override
    @Transactional
    public SubmitOrderResponseVo submitOrder(SubmitOrderVo vo) {
        orderVoThreadLocal.set(vo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        //1、校验令牌，要保证原子-
        UserItemVo userItemVo = UserLongInterceptor.thread.get();
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1])else return 0 end";
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.ORDER_TOKEN_USER_PREFIX + userItemVo.getId()), vo.getOrderToken());
        if (result == 0) {
            //1.1、校验失败
            response.setCode(1);
            return response;
        }
        //1.2、校验成功
        //1.3、创建订单，订单项等信息
        OrderCreateVo order = createOrder();
        //2、金额校验
        BigDecimal payAmount = order.getEntity().getPayAmount();
        BigDecimal payPrice = vo.getPayPrice();
        if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
            //校验成功
            //3、保存订单和订单项
            this.save(order.getEntity());
            orderItemService.saveBatch(order.getOrderItem());
            //4、封装远程查询库存所需数据
            WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
            wareSkuLockVo.setOrderSh(order.getEntity().getOrderSn());
            List<OrderItemVo> orderItemVoList = order.getOrderItem().stream().map(item -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setSkuId(item.getSkuId());
                orderItemVo.setCount(item.getSkuQuantity());
                orderItemVo.setTitle(item.getSkuName());
                return orderItemVo;
            }).collect(Collectors.toList());
            wareSkuLockVo.setLocks(orderItemVoList);
            //下订单成功，发送消息到延时队列,要确保消息一定发送出去
            try {
                rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getEntity());
            } catch (Exception e) {
                //往消息发送失败表里存储该信息
                e.printStackTrace();
            }
            //4.1、远程调用ware服务锁库存
            R r = wareFeignService.orderWareLock(wareSkuLockVo);
            if (r.getCode() == 0) {
                //锁定成功
                response.setOrder(order);
                response.setCode(0);
                return response;
            } else {
                //锁定失败
                response.setCode(3);
                return response;
            }
        } else {
            //校验失败
            response.setCode(2);
            return response;
        }
    }

    /**
     * 。
     * 创建订单信息
     *
     * @return
     */
    private OrderCreateVo createOrder() {
        OrderCreateVo vo = new OrderCreateVo();
        //1、生成订单号
        OrderEntity order = new OrderEntity();
        String orderSn = IdWorker.getTimeId();
        order.setOrderSn(orderSn);
        //2、获取运费及地址信息
        SubmitOrderVo submitOrderVo = orderVoThreadLocal.get();
        R r = wareFeignService.getFreight(submitOrderVo.getAddrId());
        FreightAndMemberItemVo data = (FreightAndMemberItemVo) r.getData(new TypeReference<FreightAndMemberItemVo>() {
        });
        //2.1、设置运费信息
        order.setFreightAmount(data.getFreight());
        //2.2、设置收货人信息
        order.setReceiverCity(data.getAddress().getCity());
        order.setReceiverName(data.getAddress().getName());
        order.setReceiverDetailAddress(data.getAddress().getDetailAddress());
        order.setReceiverPhone(data.getAddress().getPhone());
        order.setReceiverPostCode(data.getAddress().getPostCode());
        order.setReceiverProvince(data.getAddress().getProvince());
        order.setReceiverRegion(data.getAddress().getRegion());
        //3、获取购物项
        List<OrderItemVo> userCartItem = cartFeignService.getUserCartItem();
        List<OrderItemEntity> orderItems = null;
        if (userCartItem != null && userCartItem.size() > 0) {
            orderItems = userCartItem.stream().map(cartItem -> {
                OrderItemEntity orderItem = new OrderItemEntity();
                //3.1、设置订单号
                orderItem.setOrderSn(orderSn);
                //3.2、设置spu信息
                R spuInfo = productFeignService.getSpuInfoBySkuId(cartItem.getSkuId());
                SpuInfoVo spuInfoData = (SpuInfoVo) spuInfo.getData(new TypeReference<SpuInfoVo>() {
                });
                orderItem.setSpuId(spuInfoData.getId());
                orderItem.setSpuBrand(spuInfoData.getBrandId().toString());
                orderItem.setSpuName(spuInfoData.getSpuName());
                orderItem.setCategoryId(spuInfoData.getCatalogId());
                //3.3、设置sku信息
                orderItem.setSkuId(cartItem.getSkuId());
                orderItem.setSkuName(cartItem.getTitle());
                orderItem.setSkuPrice(cartItem.getPrice());
                orderItem.setSkuPic(cartItem.getImage());
                orderItem.setSkuQuantity(cartItem.getCount());
                String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
                orderItem.setSkuAttrsVals(skuAttr);
                //3.4、设置积分信息，买的越多送的越多
                orderItem.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
                orderItem.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
                //3.5、当前订单价格信息
                orderItem.setPromotionAmount(new BigDecimal("0"));
                orderItem.setIntegrationAmount(new BigDecimal("0"));
                orderItem.setCouponAmount(new BigDecimal("0"));
                //3.6、当前订单的实际金额
                BigDecimal originalPrice = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString()));
                BigDecimal newPrice = originalPrice.subtract(orderItem.getPromotionAmount()).subtract(orderItem.getIntegrationAmount()).subtract(orderItem.getCouponAmount());
                orderItem.setRealAmount(newPrice);

                return orderItem;
            }).collect(Collectors.toList());
            //4、计算订单价格,积分等相关信息
            BigDecimal total = new BigDecimal("0");
            BigDecimal coupon = new BigDecimal("0");
            BigDecimal integration = new BigDecimal("0");
            BigDecimal promotion = new BigDecimal("0");
            BigDecimal gift = new BigDecimal("0");
            BigDecimal growth = new BigDecimal("0");
            for (OrderItemEntity orderItem : orderItems) {
                coupon = coupon.add(orderItem.getCouponAmount());
                integration = integration.add(orderItem.getIntegrationAmount());
                promotion = promotion.add(orderItem.getPromotionAmount());
                total = total.add(orderItem.getRealAmount());
                gift = gift.add(new BigDecimal(orderItem.getGiftIntegration().toString()));
                growth = growth.add(new BigDecimal(orderItem.getGiftGrowth().toString()));
            }
            //4.1、设置订单价格
            order.setPromotionAmount(promotion);
            order.setCouponAmount(coupon);
            order.setIntegrationAmount(integration);
            order.setTotalAmount(total);
            //4.2、应付总额
            order.setPayAmount(total.add(order.getFreightAmount()));
            //5、订单状态
            order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
            order.setAutoConfirmDay(7);//7天订单自动确认
            //6、订单价格积分信息
            order.setIntegration(gift.intValue());
            order.setGrowth(growth.intValue());
            //7、设置其他数据
            order.setMemberUsername(data.getAddress().getName());
            order.setMemberId(data.getAddress().getMemberId());
            order.setCreateTime(new Date());
        }
        vo.setEntity(order);
        vo.setOrderItem(orderItems);
        return vo;
    }

    /**
     * 根据订单号查询订单状态
     *
     * @param orderSh
     * @return
     */
    @Override
    public OrderEntity queryOrderStatusByOrderSh(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    //关闭订单
    @Override
    public void closeOrder(OrderEntity order) {
        //1、修改订单状态
        OrderEntity updateOrder = new OrderEntity();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(OrderStatusEnum.CANCLED.getCode());
        this.updateById(updateOrder);
        //2、给库存队列发送消息
        OrderTo orderTo = new OrderTo();
        BeanUtils.copyProperties(order, orderTo);
        rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
    }

    /**
     * 查询订单详细信息
     *
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPayItemByOrderSn(String orderSn) {
        //1、根据订单号查询订单详细信息
        OrderEntity orderEntity = this.queryOrderStatusByOrderSh(orderSn);
        //2、只要一个即可
        //3、封装信息
        PayVo pay = new PayVo();
        //4、支付宝要求精确两位小数
        BigDecimal decimal = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        pay.setTotal_amount(decimal.toString());
        pay.setOut_trade_no(orderSn);
        //5、查询订单详情
        List<OrderItemEntity> orderItemList = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItem = orderItemList.get(0);
        pay.setSubject(orderItem.getSkuName());
        pay.setBody(orderItem.getSkuAttrsVals());
        return pay;
    }

    /**
     * 用户订单页面
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryOrderWithOrderItem(Map<String, Object> params) {
        //1、构建数据
        UserItemVo userItemVo = UserLongInterceptor.thread.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",
                        userItemVo.getId()).orderByDesc("id"));
        //2、构建订单详情
        if(page.getRecords()!=null && page.getRecords().size()>0){
            List<OrderEntity> orderEntities = page.getRecords().stream().map(item -> {
                List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", item.getOrderSn()));
                item.setOrderItemEntityList(orderItemEntities);
                return item;
            }).collect(Collectors.toList());
            page.setRecords(orderEntities);
        }
        return new PageUtils(page);
    }

    /**
     * 订单支付成功的回调方法
     * @param vo
     * @return
     */
    @Override
    @Transactional
    public String handleOrderPay(PayAsyncVo vo) {
        //1、保存订单支付信息
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setCreateTime(new Date());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setTotalAmount(new BigDecimal(vo.getTotal_amount()));
        paymentInfoService.save(paymentInfoEntity);
        //2、修改订单状态
        baseMapper.notifyOrderStatus(vo.getOut_trade_no(),OrderStatusEnum.PAYED.getCode());
        return "success";
    }
}

