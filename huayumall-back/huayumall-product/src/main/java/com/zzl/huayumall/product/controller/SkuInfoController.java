package com.zzl.huayumall.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.zzl.common.to.skuInfoTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.product.entity.SkuInfoEntity;
import com.zzl.huayumall.product.service.SkuInfoService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;



/**
 * sku信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 23:09:42
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 查询sku信息
     */
    @RequestMapping("/selectSkuInfo")
    public skuInfoTo selectSkuInfo(@RequestBody Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        skuInfoTo skuInfoTo = new skuInfoTo();
        BeanUtils.copyProperties(skuInfo,skuInfoTo);
        return skuInfoTo;
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @CacheEvict(value = "productItem")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @CacheEvict(value = "productItem")
    @RequestMapping("/update")
    //@RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @CacheEvict(value = "productItem")
    @RequestMapping("/delete")
    //@RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    /**
     * 根据skuId查询该商品的最新价格
     * @param skuId
     * @return
     */
    @GetMapping("/getProductPriceBySkuId/{skuId}")
    public BigDecimal getProductPriceBySkuId(@PathVariable("skuId")Long skuId){
        SkuInfoEntity byId = skuInfoService.getById(skuId);
        return byId.getPrice();
    }
}
