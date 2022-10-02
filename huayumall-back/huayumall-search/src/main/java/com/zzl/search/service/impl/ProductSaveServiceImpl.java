package com.zzl.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zzl.common.es.SkuEsModel;
import com.zzl.search.config.MySearchConfig;
import com.zzl.search.constant.EsConstant;
import com.zzl.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/01  16:11
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Resource
    private RestHighLevelClient restHighLevelClient;
    /**
     * 商品上架
     * @param skuEsModels
     */
    @Override
    public Boolean productUpSatatus(List<SkuEsModel> skuEsModels) throws IOException {
        IndexRequest indexRequest = null;
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //批量存储数据
            indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String s = JSON.toJSONString(skuEsModel);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, MySearchConfig.COMMON_OPTIONS);
        //商品存储错误
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(itme -> {
            return itme.getId();
        }).collect(Collectors.toList());
        log.error("错误id为：{}",collect);

        return b;
    }
}
