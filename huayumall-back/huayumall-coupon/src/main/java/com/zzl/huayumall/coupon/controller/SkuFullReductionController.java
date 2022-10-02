package com.zzl.huayumall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.zzl.common.to.SkuReductionTo;
import com.zzl.huayumall.coupon.entity.SkuLadderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.coupon.entity.SkuFullReductionEntity;
import com.zzl.huayumall.coupon.service.SkuFullReductionService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;



/**
 * 商品满减信息
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:28:41
 */
@RestController
@RequestMapping("coupon/skufullreduction")
public class SkuFullReductionController {
    @Autowired
    private SkuFullReductionService skuFullReductionService;

    /**
     * 保存spu的sku优惠信息
     * @param skuReductionTo
     * @return
     */
    @PostMapping("/saveInfo")
    public R saveSkuCouponAndMemberInformation(@RequestBody SkuReductionTo skuReductionTo){
        if(skuReductionTo !=null ){
            skuFullReductionService.saveInfo(skuReductionTo);
            return R.ok();
        }
       return R.error();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:skufullreduction:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuFullReductionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:skufullreduction:info")
    public R info(@PathVariable("id") Long id){
		SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

        return R.ok().put("skuFullReduction", skuFullReduction);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:skufullreduction:save")
    public R save(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.save(skuFullReduction);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:skufullreduction:update")
    public R update(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.updateById(skuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:skufullreduction:delete")
    public R delete(@RequestBody Long[] ids){
		skuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
