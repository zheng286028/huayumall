package com.zzl.huayumall.seckill.service;

import com.zzl.huayumall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/23  16:23
 */
public interface SeckillService {

    void upLoadSeckillSkuLatestThreeDays();

    List<SeckillSkuRedisTo> getCurrentTimeSeckillSkus();
}
