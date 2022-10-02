package com.zzl.huayumall.product.dao;

import com.zzl.huayumall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectAttrAndSearchByIds(@Param("attrIds") List<Long> attrIds);
}
