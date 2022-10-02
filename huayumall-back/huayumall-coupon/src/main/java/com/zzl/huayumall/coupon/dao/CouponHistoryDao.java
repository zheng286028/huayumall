package com.zzl.huayumall.coupon.dao;

import com.zzl.huayumall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:28:41
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
