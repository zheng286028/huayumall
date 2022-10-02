package com.zzl.huayumall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/08  21:25
 */
@Data
public class OrderItemVo {
    //商品id
    private Long skuId;
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

    //库存
    private boolean stock;
}
