package com.zzl.huayumall.product.vo;

import com.zzl.huayumall.product.entity.SkuImagesEntity;
import com.zzl.huayumall.product.entity.SkuInfoEntity;
import com.zzl.huayumall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/11  15:08
 */
@Data
public class SkuItemVo {
    //1、sku的基本信息
    private SkuInfoEntity info;
    //是否有货
    Boolean hasStock = true;
    //2、sku的图片信息
    private List<SkuImagesEntity> images;
    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttrs;
    //4、获取spu的介绍
    private SpuInfoDescEntity desc;
    //5、获取spu的规格参数信息
    private List<SpuItemBaseAttrGroupVo> groupAttrs;
}
