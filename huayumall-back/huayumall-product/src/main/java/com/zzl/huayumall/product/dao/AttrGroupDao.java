package com.zzl.huayumall.product.dao;

import com.zzl.huayumall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.huayumall.product.vo.SkuItemVo;
import com.zzl.huayumall.product.vo.SpuItemBaseAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemBaseAttrGroupVo> selectSpuItemBySkuIdAndCategoryId(@Param("catalogId") Long catalogId, @Param("spuId") Long spuId);
}
