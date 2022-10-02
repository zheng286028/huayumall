package com.zzl.huayumall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *  订单确认页面所需的数据
 * @author 郑子浪
 * @date 2022/09/08  21:23
 */
@Data
public class OrderConfirmVo {
    //会员地址
    private List<MemberAddressVo> address;
    //订单确认页所需的商品数据
    private List<OrderItemVo> items;
    //优惠券积分
    private Integer integration;
    //订单总额
    private BigDecimal total;
    //应付价格
    private BigDecimal payPrice;
    //库存
    private Map<Long,Boolean> stock;
    //防重令牌
    private String token;

    /**
     * 购物车商品数量
     * @return
     */
    public int getCount(){
        int count = 0;
        if(items!=null && items.size()>0){
            for (OrderItemVo item : items) {
                count = item.getCount();
            }
            return count;
        }
        return 0;
    }

    /**
     * 计算总额
     * @return
     */
    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal("0");
        //1、当前购物项价格乘于数量
        if(items!=null && items.size()>0){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                total = total.add(multiply);
            }
            return total;
        }
        return null;
    }
    /**
     * 计算应付价格
     * @return
     */
    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
