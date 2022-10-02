package com.zzl.huayumall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.product.entity.CategoryBrandRelationEntity;
import com.zzl.huayumall.product.vo.brandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveCategoryAndBrand(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategoty(Long catId, String name);

    List<brandVo> selectCategoryBeandRelationByCatId(Long catid);
}

