package com.zzl.search.controller;

import com.zzl.common.es.SkuEsModel;
import com.zzl.common.exception.BizCodeEnum;
import com.zzl.common.utils.R;
import com.zzl.search.config.MySearchConfig;
import com.zzl.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/01  16:02
 */
@Slf4j
@RestController
@RequestMapping("/search/save")
public class SkuSearch {
    @Resource
    private ProductSaveService productSaveService;

    /**
     * 保存sku的检索信息
     * @param skuEsModels
     * @return
     */
    @PostMapping("/product")
    public R saveSkuSearchInformation(@RequestBody List<SkuEsModel> skuEsModels){
        Boolean b = true;
        try {
            b = productSaveService.productUpSatatus(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架错误:{}",e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(b){
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }else {
            return R.ok();
        }
    }

}
