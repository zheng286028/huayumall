package com.zzl.huayumall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zzl.common.to.mq.OrderTo;
import com.zzl.common.to.mq.StockDetailTo;
import com.zzl.common.to.mq.StockLockTo;
import com.zzl.common.to.skuInfoTo;
import com.zzl.common.utils.R;
import com.zzl.huayumall.ware.entity.WareOrderTaskDetailEntity;
import com.zzl.huayumall.ware.entity.WareOrderTaskEntity;
import com.zzl.huayumall.ware.exception.NoStockException;
import com.zzl.huayumall.ware.feign.OrderFeignService;
import com.zzl.huayumall.ware.feign.skuFeignService;
import com.zzl.huayumall.ware.service.WareOrderTaskDetailService;
import com.zzl.huayumall.ware.service.WareOrderTaskService;
import com.zzl.huayumall.ware.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.ware.dao.WareSkuDao;
import com.zzl.huayumall.ware.entity.WareSkuEntity;
import com.zzl.huayumall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Resource
    private skuFeignService feignService;
    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * wareId: 123,//仓库id
         *    skuId: 123//商品id
         */
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId) && !"0".equalsIgnoreCase(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId) && !"0".equalsIgnoreCase(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params), wrapper

        );

        return new PageUtils(page);
    }

    /**
     * 采购入库
     *
     * @param skuId
     * @param skuNum
     * @param wareId
     */
    @Override
    public void addStock(Long skuId, Integer skuNum, Long wareId) {
        //1、如果当前库存没有则新增
        List<WareSkuEntity> skuEntities = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (skuEntities == null || skuEntities.size() == 0) {
            //新增库存
            WareSkuEntity wareSku = new WareSkuEntity();
            wareSku.setSkuId(skuId);
            wareSku.setWareId(wareId);
            wareSku.setStock(skuNum);
            wareSku.setStockLocked(0);
            try {
                //远程查询sku_name,即使出了异常，也不影响其他程序
                skuInfoTo skuInfoTo = feignService.selectSkuInfo(skuId);
                wareSku.setSkuName(skuInfoTo.getSkuName());
            } catch (Exception e) {

            }
            //新增
            this.save(wareSku);
        } else {
            //修改库存量
            wareSkuDao.updateWareSkustock(skuId, skuNum, wareId);
        }
    }

    /**
     * 根据skuId查询是否还要库存
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<hasStockSkuVo> selectWareHasSkuStock(List<Long> skuIds) {
        List<hasStockSkuVo> collect = skuIds.stream().map(skuId -> {
            hasStockSkuVo hasStockSkuVo = new hasStockSkuVo();
            //select sum(stock-stock_locked) from wms_ware_sku where sku_id = 1;
            Long count = baseMapper.selectWareHasSkuStock(skuId);
            //只要count大于0代表有库存
            hasStockSkuVo.setSkuId(skuId);
            hasStockSkuVo.setStock(count == null ? false : count > 0);
            return hasStockSkuVo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 库存锁定
     *
     * @param vo
     * @return
     */
    @Override
    @Transactional
    public Boolean orderWareLock(WareSkuLockVo vo) {
        //5、保存库存工作单信息，方便追溯
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSh());
        wareOrderTaskService.save(taskEntity);
        //1、找到每个商品都在那个仓库有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> hasStockWareIds = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setSkuId(item.getSkuId());
            //2、查询该商品在那个仓库有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(item.getSkuId(), item.getCount());
            stock.setWareIds(wareIds);
            stock.setNum(item.getCount());
            return stock;
        }).collect(Collectors.toList());
        //2、锁定库存
        for (SkuWareHasStock stock : hasStockWareIds) {
            boolean skuStock = false;
            Long skuId = stock.getSkuId();
            List<Long> wareIds = stock.getWareIds();
            //2.1、是否有仓库有此商品库存
            if (wareIds == null || wareIds.size() == 0) {
                //2.2、都没有库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //2.3、锁定库存
                Long count = wareSkuDao.skuWareLock(wareId, skuId, stock.getNum());
                if (count == 1) {
                    //2.4、锁定成功
                    skuStock = true;
                    //6、保存库存工作单详情
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
                    detailEntity.setWareId(wareId);
                    detailEntity.setSkuId(skuId);
                    detailEntity.setSkuNum(stock.getNum());
                    detailEntity.setLockStatus(1);
                    detailEntity.setTaskId(taskEntity.getId());
                    wareOrderTaskDetailService.save(detailEntity);
                    //7、发送消息
                    StockLockTo stockLockTo = new StockLockTo();
                    stockLockTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity, stockDetailTo);
                    stockDetailTo.setLockStatus(1);
                    stockLockTo.setStockDetailTo(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock-locked", stockLockTo);
                    break;
                } else {
                    //todo 库存锁定失败
                }
            }
            //3、只要有一个锁定失败，就抛异常
            if (skuStock == false) {
                throw new NoStockException(skuId);
            }
        }
        //4、全部锁定成功
        return true;
    }

    /**
     * 库存自动解锁
     */
    @Override
    public void unlockStock(StockLockTo stockLockTo) {
        //1、查询库存工作单，判断是否库存锁定成功
        WareOrderTaskEntity detailEntity = wareOrderTaskService.queryWareOrderWhitExist(stockLockTo.getId());
        if (detailEntity != null) {
            //1.1、库存锁定成功
            //2、查询订单状态是否被取消,否则无需解锁
            R r = orderFeignService.queryOrderStatusByOrderSh(detailEntity.getOrderSn());
            //3、订单存在
            OrderVo data = (OrderVo) r.getData(new TypeReference<OrderVo>() {
            });
            //4、订单不存在或者订单状态是被取消
            if (data == null || data.getStatus() == 4) {
                //4.1、订单不存在或已被取消，必须解锁库存
                WareOrderTaskDetailEntity wareOrderTaskDetail = wareOrderTaskDetailService.getById(stockLockTo.getStockDetailTo().getId());
                //4.2、只有库存工作单详情状态为锁定才需要解锁
                if (wareOrderTaskDetail.getLockStatus() == 1) {
                    unlockLockStock(wareOrderTaskDetail.getSkuId(), wareOrderTaskDetail.getWareId(), wareOrderTaskDetail.getSkuNum(), wareOrderTaskDetail.getId());
                }
            }
        }
    }

    /**
     * 订单关闭成功，解锁库存，无需等待库存自动解锁，防止网络卡顿，库存先一步解锁
     * @param orderTo
     */
    @Override
    public void unlockStock(OrderTo orderTo) {
        //1、根据订单号查询库存工作单
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderTo.getOrderSn()));
        //2、根据库存工作单详情id查询未解锁的库存
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.queryNoUnlockStockByTaskId(orderTaskEntity.getId());
        //3、循环解锁
        for (WareOrderTaskDetailEntity ware : list) {
            unlockLockStock(ware.getSkuId(),ware.getWareId(),ware.getSkuNum(),ware.getId());
        }
    }

    /**
     * 解锁被锁定的库存
     *
     * @param skuId
     * @param wareId
     * @param num
     */
    @Override
    public void unlockLockStock(Long skuId, Long wareId, int num, Long orderDetailId) {
        //解锁库存
        int i = wareSkuDao.liftLockStock(skuId, wareId, num);
        if (i > 0) {
            //解锁成功，修改库存工作单详情状态
            WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
            entity.setId(orderDetailId);
            entity.setLockStatus(2);
            wareOrderTaskDetailService.updateById(entity);
        }
    }

}
