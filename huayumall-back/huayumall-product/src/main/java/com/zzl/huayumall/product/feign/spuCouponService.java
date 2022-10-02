package com.zzl.huayumall.product.feign;

import com.zzl.common.to.SkuReductionTo;
import com.zzl.common.to.SpuBoundTo;
import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/27  17:26
 */
@FeignClient("huayumall-coupon")
public interface spuCouponService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuCoupon(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuCouponAndMemberInformation(@RequestBody SkuReductionTo skuReductionTo);
}
