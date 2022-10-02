package com.zzl.huayumall.product.dao;

import com.zzl.huayumall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品三级分类
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    //根据parentId批量查询
    List<CategoryEntity> batchSelectCategoryByCtaIds(@Param("ids") List<Long> ids);
}
