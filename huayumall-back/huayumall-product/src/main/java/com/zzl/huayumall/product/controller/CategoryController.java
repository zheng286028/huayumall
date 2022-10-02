package com.zzl.huayumall.product.controller;

import java.util.Arrays;
import java.util.List;

import com.zzl.huayumall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zzl.huayumall.product.entity.CategoryEntity;
import com.zzl.huayumall.product.service.CategoryService;
import com.zzl.common.utils.R;

import javax.annotation.Resource;


/**
 * 商品三级分类
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 23:09:42
 */
@RestController
@RequestMapping("product/category")
@Transactional
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 查询所以分类，包括子类，然后进行数形结构展示列表
     */
    @RequestMapping("/list/tree")
    //@RequiresPermissions("product:category:list")
    public R queryAllCategoryByParentCidConductClassificationTreeExhibit(){
        List<CategoryEntity> categoryEntityList = categoryService.selectCategoryByLevel();

        return R.ok().put("data", categoryEntityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @CacheEvict(value = "category",allEntries = true)
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改菜单层级
     */
    @RequestMapping("/update/sort")
    @CacheEvict(value = "category",allEntries = true)
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @CacheEvict(value = "category",allEntries = true)
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
        if(category != null){
            boolean b = categoryService.updateById(category);
            if(!b){
                throw new RuntimeException();
            }else if(!StringUtils.isEmpty(category.getName())){
                //修改成功,修改关联的字段
                Long catId = category.getCatId();
                String name = category.getName();
                categoryBrandRelationService.updateCategoty(catId,name);
            }
        }
        return R.error();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @CacheEvict(value = "category",allEntries = true)
    public R delete(@RequestBody Long[] catId){
        if(catId == null || catId.length <=0){
            return R.error("请选择删除的数据");
        }
        R r = categoryService.removeMenuByIds(Arrays.asList(catId));
        return r;
    }

}
