package com.zzl.huayumall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteAndattrRelationByIds(List<AttrAttrgroupRelationEntity> asList);
}

