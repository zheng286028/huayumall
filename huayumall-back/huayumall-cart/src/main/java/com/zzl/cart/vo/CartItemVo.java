package com.zzl.cart.vo;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能描述
 * 购物车商品项
 * @author 郑子浪
 * @date 2022/09/03  17:57
 */
@ToString
public class CartItemVo {
    //商品id
    private Long skuId;
    //是否被选中
    private Boolean check = true;
    //商品标题
    private String title;
    //图片
    private String image;
    //商品的属性信息
    private List<String> skuAttr;
    //商品价格
    private BigDecimal price;
    //商品数量
    private Integer count;
    //商品总价格
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 计算商品总价格
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
