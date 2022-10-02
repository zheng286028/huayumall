package com.zzl.huayumall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *  找到该商品在那个仓库有库存
 * @author 郑子浪
 * @date 2022/09/11  21:34
 */
@Data
public class SkuWareHasStock {
    //商品id
    private Long skuId;
    //该商品都在那个仓库有库存
    private List<Long> wareIds;
    //购买的商品数量
    private Integer num;
}
