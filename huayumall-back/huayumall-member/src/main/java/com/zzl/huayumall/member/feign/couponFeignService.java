package com.zzl.huayumall.member.feign;

import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/20  2:38
 */
@FeignClient("huayumall-coupon")
public interface couponFeignService {
    @RequestMapping("/coupon/coupon/couponMember")
    public R couponMember();
}
