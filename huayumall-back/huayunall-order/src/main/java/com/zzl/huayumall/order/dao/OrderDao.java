package com.zzl.huayumall.order.dao;

import com.zzl.huayumall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:48:59
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void notifyOrderStatus(@Param("OrderSn") String OrderSn, @Param("code") Integer code);
}
