package com.zzl.huayumall.product.dao;

import com.zzl.huayumall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<String> selectSkuAttrBySkuId(Long skuId);
}
