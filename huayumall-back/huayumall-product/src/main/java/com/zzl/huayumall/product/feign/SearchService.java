package com.zzl.huayumall.product.feign;

import com.zzl.common.es.SkuEsModel;
import com.zzl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/01  16:40
 */
@FeignClient("huayumall-search")
public interface SearchService {
    @PostMapping("/search/save/product")
    R saveSkuSearchInformation(@RequestBody List<SkuEsModel> skuEsModels);
}
