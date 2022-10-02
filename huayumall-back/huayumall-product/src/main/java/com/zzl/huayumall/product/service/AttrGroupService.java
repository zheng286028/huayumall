package com.zzl.huayumall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.product.entity.AttrGroupEntity;
import com.zzl.huayumall.product.vo.AttrGroupWithAttrVo;
import com.zzl.huayumall.product.vo.SpuItemBaseAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils selectAttrGroupByCatIdAndAnyField(Map<String, Object> params,Long id);

    PageUtils queryAttrGroupByKeyDynamic(Map<String, Object> params);

    List<AttrGroupWithAttrVo> selectAttrGroupsWithAttrsByCatId(Long catId);

    List<SpuItemBaseAttrGroupVo> selectSpuItemBySkuIdAndCategoryId(Long catalogId, Long spuId);
}

