package com.zzl.huayumall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;
import com.zzl.huayumall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    int addBrandEntity(BrandEntity brandEntity);

    int updateBrandEntityById(BrandEntity brandEntity);

    void updateStatusById(BrandEntity brand);
}

