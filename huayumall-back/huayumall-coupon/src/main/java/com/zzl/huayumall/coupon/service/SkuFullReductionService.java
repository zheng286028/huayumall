package com.zzl.huayumall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.to.SkuReductionTo;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:28:41
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveInfo(SkuReductionTo skuReductionTo);
}

