package com.zzl.huayumall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.product.entity.AttrEntity;
import com.zzl.huayumall.product.entity.ProductAttrValueEntity;
import com.zzl.huayumall.product.vo.AttrRespVo;
import com.zzl.huayumall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrAndAttrGroup(AttrVo attrvo);

    PageUtils selectByAttrList(Map<String, Object> params, Long catId, String type);

    PageUtils selectAttrGroupByKeyDynamic(Map<String, Object> params, String type);

    AttrRespVo getDetailsDate(Long attrId);

    void updateByIdAndAttrGroup(AttrRespVo attrRespVo);

    PageUtils noAttrRelation(Long attrgroupId, Map<String, Object> param);

    List<AttrEntity> selectAttrListByGroupId(Long groupId);

    void updateByspuId(Long spuId, List<ProductAttrValueEntity> valueEntities);

    List<Long> selectAttrAndSearchByIds(List<Long> attrIds);

}

