package com.zzl.huayumall.product.service.impl;

import com.zzl.common.utils.R;
import com.zzl.huayumall.product.entity.BrandEntity;
import com.zzl.huayumall.product.entity.CategoryEntity;
import com.zzl.huayumall.product.service.BrandService;
import com.zzl.huayumall.product.service.CategoryService;
import com.zzl.huayumall.product.vo.brandVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.CategoryBrandRelationDao;
import com.zzl.huayumall.product.entity.CategoryBrandRelationEntity;
import com.zzl.huayumall.product.service.CategoryBrandRelationService;

import javax.annotation.Resource;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Resource
    private BrandService brandService;
    @Resource
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存
     * @param categoryBrandRelation
     */
    @Override
    public void saveCategoryAndBrand(CategoryBrandRelationEntity categoryBrandRelation) {
        /**
         * todo,此处有问题，如果一个品牌已经和一个分类进行了关联，那么回显时/不能增加
         */
        //通过brand_id和category_id分别查询他们的name
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        //查询
        BrandEntity brandEntity = brandService.getById(brandId);
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        //存储name
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        //保存
        baseMapper.insert(categoryBrandRelation);
    }

    /**
     * 修改Brand_id的name
     * @param brandId
     * @param name
     */
    @Override
    public void updateBrand(Long brandId, String name) {

        CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
        categoryBrandRelation.setBrandName(name);

        this.update(categoryBrandRelation,new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    /**
     * 修改Category.name
     * @param catId
     * @param name
     */
    @Override
    public void updateCategoty(Long catId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setCatelogName(name);
        //根据catId修改name
        this.update(entity,new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catId));
    }

    /**
     * 根据分类id查询关联的品牌
     * @param catid
     * @return
     */
    @Override
    public List<brandVo> selectCategoryBeandRelationByCatId(Long catid) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catid));
        List<brandVo> collect = null;
        if(categoryBrandRelationEntities != null && categoryBrandRelationEntities.size()>0){
                collect = categoryBrandRelationEntities.stream().map((item) -> {
                brandVo brandVo = new brandVo();
                brandVo.setBrandId(item.getBrandId());
                brandVo.setBrandName(item.getBrandName());
                return brandVo;
            }).collect(Collectors.toList());
            return collect;
        }
        return collect;
    }
}
