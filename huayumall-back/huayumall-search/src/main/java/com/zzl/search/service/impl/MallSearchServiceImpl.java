package com.zzl.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zzl.common.es.SkuEsModel;
import com.zzl.search.config.MySearchConfig;
import com.zzl.search.constant.EsConstant;
import com.zzl.search.service.MallSearchService;
import com.zzl.search.vo.SearchParam;
import com.zzl.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/06  13:59
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Resource
    private RestHighLevelClient client;

    /**
     * 查询dls
     *
     * @param param
     * @return 模糊查询, 过滤(分类id, 品牌id, 属性的id, value匹配, 库存匹配, 价格区间), 价格排序, 分页, 高亮
     */
    @Override
    public SearchResult search(SearchParam param) {

        SearchResult result = null;
        //准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //开始查询
            SearchResponse response = client.search(searchRequest, MySearchConfig.COMMON_OPTIONS);

            //封装查询结果
            result = buildSearchResult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 准备检索请求
     *
     * @param param
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        //构造检索请求
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * 查询条件
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1、bool-must-模糊查询
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        //1.2、bool-filter-分类id
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        //1.2、bool-filter-品牌id
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        //1.2、bool-filter-属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attr : param.getAttrs()) {
                //attrs=1_18寸:15寸
                BoolQueryBuilder nestedQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0]; //属性id
                String[] attrValues = s[1].split(":");//属性的值
                //nested-term
                nestedQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                //nested-terms
                nestedQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                //每遍历一次生成一个nested对象
                NestedQueryBuilder nested = QueryBuilders.nestedQuery("attrs", nestedQuery, ScoreMode.None);
                boolQuery.filter(nested);
            }
        }
        //1.2、bool-filter-库存
        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termsQuery("hasStock", param.getHasStock() == 1));
        }
        //1.2、bool-filter-价格区间
        //1_500/_500/500_
        //todo 这里出错了，“_“分割后”“也算length，解决办法就是怎么判定“”不能算长度
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                //区间
                rangeQuery.gte(s[0]).lte(s[1]);
            } else {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }

            }
            boolQuery.filter(rangeQuery);
        }
        //收集查询检索条件
        searchSourceBuilder.query(boolQuery);
        /**
         * 排序，分页
         */
        //2.1、排序
        if (!StringUtils.isEmpty(param.getSort())) {
            //sort=hotscore_desc/asc
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }
        //2.2、分页
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        /**
         * 高亮
         */
        //3.1
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合条件
         */
        //3.1、构建品牌聚合
        TermsAggregationBuilder brandId_aggs = AggregationBuilders.terms("brandId_aggs");
        brandId_aggs.field("brandId").size(50);
        //3.1、品牌的子聚合
        brandId_aggs.subAggregation(AggregationBuilders.terms("brand_name_aggs").field("brandName").size(1));
        brandId_aggs.subAggregation(AggregationBuilders.terms("brand_img_aggs").field("brandImg").size(1));
        //品牌聚合
        searchSourceBuilder.aggregation(brandId_aggs);
        //3.2、分类聚合
        TermsAggregationBuilder catalogId_aggs = AggregationBuilders.terms("catalogId_aggs");
        catalogId_aggs.field("catalogId").size(20);
        //3.2、分类的子聚合
        catalogId_aggs.subAggregation(AggregationBuilders.terms("catelog_name_aggs").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalogId_aggs);
        //3.3、属性聚合
        //3.3.1、属性大聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attrs_aggs", "attrs");
        //3.3.2、属性id聚合
        TermsAggregationBuilder attrId_aggs = AggregationBuilders.terms("attrId_aggs").field("attrs.attrId").size(20);
        //3.3.3、属性id聚合的子聚合
        attrId_aggs.subAggregation(AggregationBuilders.terms("attr_name_aggs").field("attrs.attrName"));
        attrId_aggs.subAggregation(AggregationBuilders.terms("attr_values_aggs").field("attrs.attrValue").size(20));
        //属性的id聚合
        nested.subAggregation(attrId_aggs);
        //属性聚合
        searchSourceBuilder.aggregation(nested);


        System.out.println("检索条件为：" + searchSourceBuilder.toString());

        //收集所有检索条件
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        return searchRequest;
    }

    /**
     * 封装结果
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        SearchHits hits = response.getHits();
        SearchHit[] hit = hits.getHits();
        //封装检索到的数据
        SearchResult result = new SearchResult();

        //1、商品信息
        if (hit != null && hit.length > 0) {
            List<SkuEsModel> skuEsModels = new ArrayList<>();
            for (SearchHit searchHit : hit) {
                String sourceAsString = searchHit.getSourceAsString();
                //将商品信息转成对象
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                //收集
                skuEsModels.add(skuEsModel);
                //设置高亮的值
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    HighlightField skuTitle = searchHit.getHighlightFields().get("skuTitle");
                    String SkuTitle = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(SkuTitle);
                }
            }
            result.setProduct(skuEsModels);
        }

        //2、属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested aggregation = response.getAggregations().get("attrs_aggs");
        ParsedLongTerms attrIdAggs = aggregation.getAggregations().get("attrId_aggs");
        SearchResult.AttrVo attr = null;

        for (Terms.Bucket bucket : attrIdAggs.getBuckets()) {
            attr = new SearchResult.AttrVo();
            //属性id
            Long attrId = bucket.getKeyAsNumber().longValue();
            //属性名称
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_aggs")).getBuckets().get(0).getKeyAsString();
            //属性Value
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_values_aggs")).getBuckets().stream().map(item -> {
                String keyAsString = item.getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());
            //收集
            attr.setAttrId(attrId);
            attr.setAttrName(attrName);
            attr.setAttrValue(attrValues);
            attrVos.add(attr);
        }
        result.setAttrs(attrVos);
        //3、分类信息
        ParsedLongTerms categoryIdAggs = response.getAggregations().get("catalogId_aggs");
        if (!StringUtils.isEmpty(categoryIdAggs)) {
            List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
            List<? extends Terms.Bucket> buckets = categoryIdAggs.getBuckets();
            SearchResult.CatalogVo catalogVo = null;
            for (Terms.Bucket bucket : buckets) {
                //收集分类id
                catalogVo = new SearchResult.CatalogVo();
                String keyAsString = bucket.getKeyAsString();
                catalogVo.setCatalogId(Long.parseLong(keyAsString));
                //收集分类名称
                ParsedStringTerms categoryNameAggsame = bucket.getAggregations().get("catelog_name_aggs");
                String categoryName = categoryNameAggsame.getBuckets().get(0).getKeyAsString();
                catalogVo.setCatalogName(categoryName);
                //收集
                catalogVos.add(catalogVo);
            }
            result.setCatalogs(catalogVos);
        }
        //4、品牌信息
        ParsedLongTerms brandIdAggs = response.getAggregations().get("brandId_aggs");
        if (!StringUtils.isEmpty(brandIdAggs)) {
            List<SearchResult.BrandVo> brandVos = new ArrayList<>();
            List<? extends Terms.Bucket> buckets = brandIdAggs.getBuckets();
            SearchResult.BrandVo brandVo = null;
            for (Terms.Bucket bucket : buckets) {
                brandVo = new SearchResult.BrandVo();
                //收集品牌id
                long brandId = bucket.getKeyAsNumber().longValue();
                //品牌名称
                String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_aggs")).getBuckets().get(0).getKeyAsString();
                //品牌图片路径
                String brandImages = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_aggs")).getBuckets().get(0).getKeyAsString();
                //收集
                brandVo.setBrandId(brandId);
                brandVo.setBrandName(brandName);
                brandVo.setBrandImg(brandImages);
                brandVos.add(brandVo);
            }
            result.setBrands(brandVos);
        }

        //5、分页信息
        Long total = hits.getTotalHits().value;
        result.setTotal(total);
        result.setPageNum(param.getPageNum());
        int totalPage = (int) (total % EsConstant.PRODUCT_PAGE_SIZE == 0 ? total / EsConstant.PRODUCT_PAGE_SIZE : (total / EsConstant.PRODUCT_PAGE_SIZE + 1));
        result.setTotalPages(totalPage);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        /*=============获取面包屑数据============*/
        //1、属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> attrNavs = param.getAttrs().stream().map(item -> {
                String[] s = item.split("_");
                Long attrId = Long.valueOf(s[0]);
                //存储attrId，方便页面指定了对应的属性，就可以不显示对应的属性
                result.getAttrIds().add(attrId);
                //调用方法获取attrName
                List<SearchResult.AttrVo> attrvo = selectAttrNameByAttrId(attrId, result.getAttrs());
                SearchResult.NavVo nav = null;
                //遍历比较过的attr封装数据
                for (SearchResult.AttrVo attrVo : attrvo) {
                    nav = new SearchResult.NavVo();
                    nav.setNavName(attrVo.getAttrName());
                    nav.setNavValue(s[1]);
                }
                String encode = null;
                try {
                    encode = URLEncoder.encode(item, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = param.get_queryString().replace("&attrs=" + encode, "");
                nav.setLink("http://search.huayumall.com/list.html?" + replace);

                return nav;
            }).collect(Collectors.toList());
            //存储属性面包屑数据
            result.setNavs(attrNavs);
        }
        //2、品牌
        if (param.getBrandId() != null && param.getBrandId().size() > 0 && result.getBrands() != null && result.getBrands().size() > 0) {
            SearchResult.NavVo navVo = null;
            //获取新的list集合，防止多个面包屑数据覆盖
            List<SearchResult.NavVo> navs = result.getNavs();
            //根据id比较
            List<SearchResult.BrandVo> brandVoList = result.getBrands().stream().filter(item -> item.getBrandId().longValue() == param.getBrandId().get(0).longValue()).collect(Collectors.toList());
            //遍历比较后的结果
            for (SearchResult.BrandVo brand : brandVoList) {
                //收集品牌面包屑所需的数据
                navVo = new SearchResult.NavVo();
                navVo.setNavName(brand.getBrandName());
                navVo.setNavValue("品牌");
                //数字不需要解码
                String replace = param.get_queryString().replace("&brandId=" + param.getBrandId().get(0), "");
                navVo.setLink("http://search.huayumall.com/list.html?" + replace);
                navs.add(navVo);
            }
            result.setNavs(navs);
        }

        //3、分类
        if (param.getCatalog3Id() > 0) {
            //收集分类名称,根据id比较
            List<SearchResult.CatalogVo> collect = result.getCatalogs().stream().filter(item -> item.getCatalogId().longValue() == param.getCatalog3Id().longValue()).collect(Collectors.toList());
            //遍历结果
            SearchResult.NavVo navVo = null;
            for (SearchResult.CatalogVo catalog : collect) {
                navVo = new SearchResult.NavVo();
                navVo.setNavName("分类");
                navVo.setNavValue(catalog.getCatalogName());
                result.getNavs().add(navVo);
            }
        }
        return result;
    }

    /**
     * 获取面包屑所需的attrName:根据attrId比较
     *
     * @param attrId
     * @param attrs
     * @return
     */
    private List<SearchResult.AttrVo> selectAttrNameByAttrId(Long attrId, List<SearchResult.AttrVo> attrs) {
        List<SearchResult.AttrVo> collect = attrs.stream().filter(item -> item.getAttrId().longValue() == attrId.longValue()).collect(Collectors.toList());
        return collect;
    }

}
