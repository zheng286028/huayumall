package com.zzl.huayumall.seckill.controller;

import com.zzl.common.utils.R;
import com.zzl.huayumall.seckill.service.SeckillService;
import com.zzl.huayumall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/25  13:59
 */
@RestController
public class SeckillController {
    @Autowired
    private SeckillService seckillService;

    /**
     * 获取当前时间段的秒杀商品
     * @return
     */
    @GetMapping("/currentSeckill")
    public R getCurrentTimeSeckillSkus(){
        List<SeckillSkuRedisTo> currentTimeSeckillSkus = seckillService.getCurrentTimeSeckillSkus();
        return R.ok().setData(currentTimeSeckillSkus);
    }
}
