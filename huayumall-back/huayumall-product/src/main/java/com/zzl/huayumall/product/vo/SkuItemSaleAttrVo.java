package com.zzl.huayumall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/12  14:59
 */
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWhitSkuIdVo> attrValue;
}
