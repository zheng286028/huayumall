package com.zzl.cart.feign;

import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/04  17:00
 */
@FeignClient("huayumall-product")
public interface ProductService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/getSkuAttrBySkuId/{skuId}")
    List<String> getSkuAttrBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("/product/skuinfo/getProductPriceBySkuId/{skuId}")
    BigDecimal getProductPriceBySkuId(@PathVariable("skuId")Long skuId);
}
