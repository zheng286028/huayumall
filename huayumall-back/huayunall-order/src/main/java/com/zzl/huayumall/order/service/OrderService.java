package com.zzl.huayumall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.order.entity.OrderEntity;
import com.zzl.huayumall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:48:59
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(SubmitOrderVo vo);

    OrderEntity queryOrderStatusByOrderSh(String orderSh);

    void closeOrder(OrderEntity order);

    PayVo getOrderPayItemByOrderSn(String orderSn);

    PageUtils queryOrderWithOrderItem(Map<String, Object> params);

    String handleOrderPay(PayAsyncVo vo);
}

