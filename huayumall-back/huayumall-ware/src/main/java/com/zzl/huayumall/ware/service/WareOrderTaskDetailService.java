package com.zzl.huayumall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.ware.entity.WareOrderTaskDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 库存工作单
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:57:18
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<WareOrderTaskDetailEntity> queryNoUnlockStockByTaskId(Long id);
}

