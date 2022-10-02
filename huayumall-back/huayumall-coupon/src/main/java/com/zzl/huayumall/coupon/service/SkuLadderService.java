package com.zzl.huayumall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:28:41
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

