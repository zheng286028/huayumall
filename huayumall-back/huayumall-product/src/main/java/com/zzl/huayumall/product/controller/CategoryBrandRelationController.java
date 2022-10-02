package com.zzl.huayumall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzl.huayumall.product.vo.brandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zzl.huayumall.product.entity.CategoryBrandRelationEntity;
import com.zzl.huayumall.product.service.CategoryBrandRelationService;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 23:09:42
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 根据catId查询与之关联的品牌信息
     * @param catid
     * @return
     */
    @GetMapping("/brands/list")
    public R selectBrandsAndBrandNameByCatIdList(@RequestParam("catId")Long catid){
        List<brandVo> brand = categoryBrandRelationService.selectCategoryBeandRelationByCatId(catid);
        return R.ok().put("data",brand);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @GetMapping("/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R queryBrandAndCategoryByBrandId(@RequestParam(value = "brandId")Long brandId){
        List<CategoryBrandRelationEntity> page = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>()
                .eq("brand_id", brandId)
        );
        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveCategoryAndBrand(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
