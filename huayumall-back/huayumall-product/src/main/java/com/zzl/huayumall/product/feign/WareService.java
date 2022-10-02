package com.zzl.huayumall.product.feign;

import com.zzl.common.to.hasStockSkuVo;
import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/01  15:12
 */
@FeignClient("huayumall-ware")
public interface WareService {
    @PostMapping("/ware/waresku/hasSkuStock")
    List<hasStockSkuVo> selectWareHasSkuStock(@RequestBody List<Long> skuId);
}
