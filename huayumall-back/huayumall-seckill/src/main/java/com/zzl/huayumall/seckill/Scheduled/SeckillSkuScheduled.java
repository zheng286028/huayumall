package com.zzl.huayumall.seckill.Scheduled;

import com.zzl.huayumall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/23  16:19
 */
@Slf4j
@Service
public class SeckillSkuScheduled {
    @Resource
    private SeckillService seckillService;

    /**
     * 上架三天的商品
     */
    @Scheduled(cron = " */5 * * * * ? ")
    public void upLoadSeckillSkuLatestThreeDays(){
        log.info("开始上架。。。。。。、");
        seckillService.upLoadSeckillSkuLatestThreeDays();
    }
}
