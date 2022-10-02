package com.zzl.huayumall.product.dao;

import com.zzl.huayumall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

    int addBrandEntity(BrandEntity brandEntity);

    int updateStatusById(BrandEntity brandEntity);
}
