package com.zzl.search.service;

import com.zzl.common.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/01  16:10
 */
public interface ProductSaveService {
    Boolean productUpSatatus(List<SkuEsModel> skuEsModels) throws IOException;

}
