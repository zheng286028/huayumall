package com.zzl.huayumall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.order.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:48:59
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

