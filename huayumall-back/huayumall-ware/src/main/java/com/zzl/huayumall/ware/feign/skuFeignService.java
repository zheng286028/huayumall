package com.zzl.huayumall.ware.feign;

import com.zzl.common.to.skuInfoTo;
import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/29  20:08
 */
@Component
@FeignClient("huayumall-product")
public interface skuFeignService {
    //存储sku名称
    @RequestMapping("/product/skuinfo/selectSkuInfo")
    skuInfoTo selectSkuInfo(@RequestBody Long skuId);
}
