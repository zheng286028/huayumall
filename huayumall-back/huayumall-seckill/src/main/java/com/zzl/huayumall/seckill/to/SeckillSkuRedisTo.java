package com.zzl.huayumall.seckill.to;

import com.zzl.huayumall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/23  17:57
 */
@Data
public class SeckillSkuRedisTo {
    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;

    /**
     * 商品的随机码
     *
     */
    private String randomCode;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    /**
     * sku的基本信息
     */

    private SkuInfoVo skuinfo;

    /**
     * 秒杀的开始时间
     */
    private Long startTime;

    /**
     * 秒杀的结束时间
     */
    private Long endTime;
}
