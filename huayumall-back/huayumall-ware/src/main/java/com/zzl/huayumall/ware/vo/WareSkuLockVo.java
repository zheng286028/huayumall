package com.zzl.huayumall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/11  20:55
 */
@Data
public class WareSkuLockVo {
    //订单号
    private String orderSh;
    //需要锁定的商品信息
    private List<OrderItemVo> locks;
}
