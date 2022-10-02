package com.zzl.huayumall.order.feign;

import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/11  17:39
 */
@FeignClient("huayumall-product")
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/spuInfo/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId")Long skuId);
}
