package com.zzl.huayumall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:57:18
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

