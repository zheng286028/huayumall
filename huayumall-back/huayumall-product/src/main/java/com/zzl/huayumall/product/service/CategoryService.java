package com.zzl.huayumall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;
import com.zzl.huayumall.product.entity.CategoryEntity;
import com.zzl.huayumall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> selectCategoryByLevel();

    R removeMenuByIds(List<Long> asList);

    Long[] findCategoryPath(Long catelogId);

    List<CategoryEntity> selectDelevlCategoryOne();

    Map<String, List<Catelog2Vo>> getCategoryJson();

}


