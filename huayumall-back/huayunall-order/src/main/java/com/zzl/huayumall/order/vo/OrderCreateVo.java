package com.zzl.huayumall.order.vo;

import com.zzl.huayumall.order.entity.OrderEntity;
import com.zzl.huayumall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/11  16:30
 */
@Data
public class OrderCreateVo {
    private OrderEntity entity;
    private List<OrderItemEntity> orderItem;
    private BigDecimal payPrice;
    private BigDecimal freight;
}
