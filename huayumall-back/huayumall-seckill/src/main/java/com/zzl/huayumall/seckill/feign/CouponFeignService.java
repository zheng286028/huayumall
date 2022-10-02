package com.zzl.huayumall.seckill.feign;

import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/23  16:25
 */
@FeignClient("huayumall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/seckillsession/getLatesThreeDaySession")
    R getLatesThreeDaySession();
}
