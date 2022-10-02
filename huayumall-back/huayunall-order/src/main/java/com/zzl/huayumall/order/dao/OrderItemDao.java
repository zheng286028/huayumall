package com.zzl.huayumall.order.dao;

import com.zzl.huayumall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单项信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:48:59
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

    List<OrderItemEntity> queryOrderItemByOrderSn(@Param("orderSnList") List<String> orderSnList);
}
