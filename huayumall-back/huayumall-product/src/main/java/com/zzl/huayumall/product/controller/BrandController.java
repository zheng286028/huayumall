package com.zzl.huayumall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.zzl.common.Valid.AddGroup;
import com.zzl.common.Valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zzl.huayumall.product.entity.BrandEntity;
import com.zzl.huayumall.product.service.BrandService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;


/**
 * 品牌
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 23:09:42
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
        if(brandId==0){
            throw new NullPointerException();
        }
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(value = {AddGroup.class}) @RequestBody BrandEntity brand){
            brandService.save(brand);
            return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(value = {UpdateGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateStatusById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
        for(Long a:brandIds){
            if(a == null){
                throw new NullPointerException();
            }
        }
        brandService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }
}

