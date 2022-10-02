package com.zzl.huayumall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.product.entity.SpuInfoEntity;
import com.zzl.huayumall.product.vo.Attr;
import com.zzl.huayumall.product.vo.SkuItemSaleAttrVo;
import com.zzl.huayumall.product.vo.SpuSaveVo;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBeanSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void productUp(Long spuId);

    List<SkuItemSaleAttrVo> querySkuBySpuId(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

