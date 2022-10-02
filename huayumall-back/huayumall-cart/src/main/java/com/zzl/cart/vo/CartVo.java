package com.zzl.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能描述
 *  购物车
 * @author 郑子浪
 * @date 2022/09/03  17:57
 */
public class CartVo {
    //购物车商品项
    List<CartItemVo> cartItemVos;
    //购物车商品数量
    private Integer countNum;
    //购物车商品类型
    private Integer countByte;
    //购物车商品总价格
    private BigDecimal totalPrice;
    //购物车商品减免价格
    private BigDecimal reduce;

    //get,set

    public List<CartItemVo> getCartItemVos() {
        return cartItemVos;
    }

    public void setCartItemVos(List<CartItemVo> cartItemVos) {
        this.cartItemVos = cartItemVos;
    }

    /**
     * 计算购物车商品数量
     * @return
     */
    public Integer getCountNum() {
        int count = 0;
        if(cartItemVos !=null && cartItemVos.size()>0){
            //有商品
            for (CartItemVo item : cartItemVos) {
                count+=item.getCount();
            }
        }
        return count;
    }

    /**
     * 计算购物车商品类型有多少
     * @return
     */
    public Integer getCountByte() {
        int count = 0;
        if(cartItemVos !=null && cartItemVos.size()>0){
            //有商品
            for (CartItemVo item : cartItemVos) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * 计算购物车商品总价格
     * @return
     */
    public BigDecimal getTotalPrice() {
        BigDecimal decimal = new BigDecimal("0.00");
        if(cartItemVos !=null && cartItemVos.size()>0){
            //1、计算总价
            for (CartItemVo item : cartItemVos) {
                if(item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    decimal = decimal.add(totalPrice);
                }
            }
        }
        //2、减去优惠 ,todo：这里可能有问题
        if(reduce != null){
            BigDecimal subtract = decimal.subtract(getReduce());
            return subtract;
        }
        return decimal;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
