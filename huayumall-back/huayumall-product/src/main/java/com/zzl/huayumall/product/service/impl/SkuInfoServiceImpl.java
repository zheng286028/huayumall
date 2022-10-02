package com.zzl.huayumall.product.service.impl;

import com.zzl.huayumall.product.entity.SkuImagesEntity;
import com.zzl.huayumall.product.entity.SpuInfoDescEntity;
import com.zzl.huayumall.product.service.*;
import com.zzl.huayumall.product.vo.Attr;
import com.zzl.huayumall.product.vo.SkuItemSaleAttrVo;
import com.zzl.huayumall.product.vo.SkuItemVo;
import com.zzl.huayumall.product.vo.SpuItemBaseAttrGroupVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.SkuInfoDao;
import com.zzl.huayumall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Resource
    private SkuImagesService skuImagesService;
    @Resource
    private SpuInfoDescService spuInfoDescService;
    @Resource
    private AttrGroupService attrGroupService;
    @Resource
    private SpuInfoService spuInfoService;
    @Resource
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBeanSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         * key: '华为',//检索关键字
         * catelogId: 0,
         * brandId: 0,
         * min: 0,
         * max: 0
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(item -> {
                item.eq("id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catrlog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min) && new BigDecimal(min).compareTo(BigDecimal.ZERO) > 0) {
            wrapper.le("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && new BigDecimal(max).compareTo(BigDecimal.ZERO) > 0) {
            wrapper.ge("price", max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params), wrapper

        );

        return new PageUtils(page);
    }

    /**
     * 商品的详细信息
     *
     * @param skuId
     * @return
     */
    @Override
    @Cacheable(value = "productItem",key ="#skuId")
    public SkuItemVo selectProductItemBySkuId(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku的基本信息
            SkuInfoEntity skuInfo = getById(skuId);
            skuItemVo.setInfo(skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((resp) -> {
            //3、获取spu的销售属性组合
            List<SkuItemSaleAttrVo> saleAttrVo = spuInfoService.querySkuBySpuId(resp.getSpuId());
            skuItemVo.setSaleAttrs(saleAttrVo);
        }, executor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((resp) -> {
            //4、获取spu的介绍
            SpuInfoDescEntity desc = spuInfoDescService.getById(resp.getSpuId());
            skuItemVo.setDesc(desc);
        }, executor);

        CompletableFuture<Void> groupAttrFuture = infoFuture.thenAcceptAsync((resp) -> {
            //5、获取spu的规格参数信息
            List<SpuItemBaseAttrGroupVo> groupAttrs = attrGroupService.selectSpuItemBySkuIdAndCategoryId(resp.getCatalogId(), resp.getSpuId());
            skuItemVo.setGroupAttrs(groupAttrs);
        });

        CompletableFuture<Void> imagFuture = CompletableFuture.runAsync(() -> {
            //2、sku的图片信息
            List<SkuImagesEntity> images = skuImagesService.selectBySkuId(skuId);
            skuItemVo.setImages(images);
        });
        //等待全部任务完成
        CompletableFuture.allOf(infoFuture,saleAttrFuture,descFuture,groupAttrFuture,imagFuture).get();

        return skuItemVo;
    }

}
