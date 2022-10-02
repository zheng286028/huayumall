package com.zzl.huayumall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.SkuImagesDao;
import com.zzl.huayumall.product.entity.SkuImagesEntity;
import com.zzl.huayumall.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveListBeanSkuImages(List<SkuImagesEntity> skuImages) {
        if(!skuImages.isEmpty() && skuImages.size()>0 ){
            this.saveBatch(skuImages);
        }
    }

    @Override
    public List<SkuImagesEntity> selectBySkuId(Long skuId) {
        List<SkuImagesEntity> imagesEntities = list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        return imagesEntities;
    }

}
