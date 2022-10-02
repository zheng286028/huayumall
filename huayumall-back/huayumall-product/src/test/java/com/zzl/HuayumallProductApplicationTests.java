package com.zzl;

import com.zzl.huayumall.product.service.AttrGroupService;
import com.zzl.huayumall.product.service.BrandService;
import com.zzl.huayumall.product.service.CategoryService;
import com.zzl.huayumall.product.service.SpuInfoService;
import com.zzl.huayumall.product.vo.Attr;
import com.zzl.huayumall.product.vo.SkuItemSaleAttrVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuayumallProductApplicationTests {
    @Resource
    BrandService brandService;
    @Resource
    private StringRedisTemplate redis;
    @Resource
    CategoryService categoryService;
    @Resource
    private RedissonClient client;
    @Resource
    private AttrGroupService attrGroupService;
    @Resource
    SpuInfoService spuInfoService;

    @Test
    public void test(){
        List<SkuItemSaleAttrVo> saleAttrVo = spuInfoService.querySkuBySpuId(9L);
        System.out.println(saleAttrVo);
    }

}
