package com.zzl.search;

import com.alibaba.fastjson.JSON;
import com.zzl.search.config.MySearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuayumallSearchApplicationTests {
    @Resource
    private RestHighLevelClient rest;


    //检索
    @Test
    public void searchData() throws IOException {
        //不指定索引默认全部
        SearchRequest searchRequest = new SearchRequest("bank");
        //索引条件
        SearchSourceBuilder search = new SearchSourceBuilder();
        SearchSourceBuilder query = search.query(QueryBuilders.matchQuery("address","mall"));
        //年龄分布
        TermsAggregationBuilder ageAggs = AggregationBuilders.terms("ageAggs").field("age").size(10);
        //薪资平均值
        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("balanceAggs").field("balance");
        search.aggregation(ageAggs);
        search.aggregation(avgAggregationBuilder);
        System.out.println(search.toString());
        searchRequest.source(query);
        System.out.println("===================================");

        //执行检索
        SearchResponse response = rest.search(searchRequest, MySearchConfig.COMMON_OPTIONS);
        //结果
        System.out.println(response.toString());
    }

    //添加
    @Test
    public void index() throws IOException {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.id("1");
        indexRequest.index("users");
        User user = new User();
        user.setUserName("李四");
        user.setGender("男");
        user.setAge(18);
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);//存储的内容。只能是json
        rest.index(indexRequest, MySearchConfig.COMMON_OPTIONS);

    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    public void contextLoads() {
        System.out.println(rest);
    }

}
