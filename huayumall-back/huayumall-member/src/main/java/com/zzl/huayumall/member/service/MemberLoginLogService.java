package com.zzl.huayumall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:40:26
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

