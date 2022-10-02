package com.zzl.huayumall.product.service.impl;

import com.zzl.common.utils.R;
import com.zzl.huayumall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.BrandDao;
import com.zzl.huayumall.product.entity.BrandEntity;
import com.zzl.huayumall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("brandService")
@Transactional
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    private BrandDao brandDao;
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    //分页
    public PageUtils queryPage(Map<String, Object> params) {
        //查询条件
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<BrandEntity>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.eq("brand_id",key).or().like("name",key);
            }
        //分页条件
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );
        //返回完成分页的数据
        return new PageUtils(page);
    }

    @Override
    public int addBrandEntity(BrandEntity brandEntity) {
        return brandDao.addBrandEntity(brandEntity);
    }
    @Override
    public int updateBrandEntityById(BrandEntity brandEntity){
        return brandDao.updateById(brandEntity);
    }

    /**
     * 根据id修改品牌，同时修改关联的字段
     * @param brand
     */
    @Override
    public void updateStatusById(BrandEntity brand) {
        int item = baseMapper.updateStatusById(brand);
        if(item<=0){
            throw new RuntimeException();
        }else if(!StringUtils.isEmpty(brand.getName())){
            //更新完成，同时也要更新其他关联的字段
            Long brandId = brand.getBrandId();
            String name = brand.getName();
            categoryBrandRelationService.updateBrand(brandId,name);
        }
    }
}
