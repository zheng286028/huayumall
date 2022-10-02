package com.zzl.huayumall.product.service.impl;

import com.zzl.common.constant.productConstant;
import com.zzl.common.es.SkuEsModel;
import com.zzl.common.to.SkuReductionTo;
import com.zzl.common.to.SpuBoundTo;
import com.zzl.common.to.hasStockSkuVo;
import com.zzl.common.utils.R;
import com.zzl.huayumall.product.entity.*;
import com.zzl.huayumall.product.feign.SearchService;
import com.zzl.huayumall.product.feign.WareService;
import com.zzl.huayumall.product.feign.spuCouponService;
import com.zzl.huayumall.product.service.*;
import com.zzl.huayumall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    private spuCouponService couponService;
    @Resource
    private BrandService brandService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private WareService wareService;
    @Resource
    private SearchService searchService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息  pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        //封装数据
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoEntity.setCreateTime(new Date());
        this.saveBeanSpuInfo(spuInfoEntity);

        //2、保存spu的描述图片 pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        List<String> decript = vo.getDecript();
        //封装数据
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript ));
        spuInfoDescService.saveBeanSpuInfoDesc(spuInfoDescEntity);

        //3、保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveSpuBean(spuInfoEntity.getId(),images);

        //4、保存spu的规格参数  pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        if(!baseAttrs.isEmpty() || baseAttrs.size()>0){

            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                //封装数据
                productAttrValueEntity.setAttrId(attr.getAttrId());
                productAttrValueEntity.setQuickShow(attr.getShowDesc());
                productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                productAttrValueEntity.setAttrValue(attr.getAttrValues());
                //查询属性名称
                AttrEntity attrBean = attrService.getById(attr.getAttrId());
                productAttrValueEntity.setAttrName(attrBean.getAttrName());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            //保存
            productAttrValueService.saveBeanProductAttrValue(collect);
        }
        //6、保存spu的积分信息 sms_spu_bounds
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        Bounds bounds = vo.getBounds();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        //调用远程服务完成保存
        R r = couponService.saveSpuCoupon(spuBoundTo);
        if(r.getCode() !=0) {
            log.info("调用的远程会员服务失败");
        }
        //5、保存当前spu对应的所有sku信息
        //5.1)、sku的基本信息 pms_sku_info
        List<Skus> skus = vo.getSkus();
        if(!skus.isEmpty() && skus.size()>0){
            skus.forEach(item->{
                //获取默认图片
                List<Images> img = item.getImages();
                String defaultImg = null;
                for (Images imgs : img) {
                    if(imgs.getDefaultImg() == 1){
                        defaultImg = imgs.getImgUrl();
                    }
                }
                log.info("默认的图片路径为：{}",defaultImg);
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                //封装参数
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setBrandId(vo.getBrandId());
                skuInfoEntity.setCatalogId(vo.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //保存
                skuInfoService.saveBeanSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();
                log.info("SkuId：{}",skuId);
                //保存默认图片
                List<SkuImagesEntity> SkuImages = item.getImages().stream().map(ima -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(ima.getImgUrl());
                    skuImagesEntity.setDefaultImg(ima.getDefaultImg());
                    return skuImagesEntity;
                }).filter(filimgs->{
                    //返回true收集，false过滤
                    return !StringUtils.isEmpty(filimgs.getImgUrl());
                }).collect(Collectors.toList());
                //5.2)、sku的图片信息 pms_sku_images
                skuImagesService.saveListBeanSkuImages(SkuImages);
                //5.3)、sku的销售属性基本信息 pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                //封装参数
                List<SkuSaleAttrValueEntity> skuSaleAttrValueList = attr.stream().map(at -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(at, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                //批量保存
                skuSaleAttrValueService.saveBeanListSkuSaleAttrValue(skuSaleAttrValueList);
                //5.4)、sku的优惠，满减信息 sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //调用服务，完成保存
                //如果优惠为0就没必要保存了
                if(item.getFullCount()>0 || item.getFullPrice().compareTo(new BigDecimal("0"))==1) {
                    R coupon = couponService.saveSkuCouponAndMemberInformation(skuReductionTo);
                    if (coupon.getCode() != 0) {
                        log.info("远程调用会员服务失败！");
                    }
                }
            });
        }
    }

    /**
     * 保存spu基本信息
     * @param spuInfoEntity
     */
    @Override
    public void saveBeanSpuInfo(SpuInfoEntity spuInfoEntity) {
        baseMapper.insert(spuInfoEntity);
    }

    /**
     *   key: '华为',//检索关键字
     *    catelogId: 6,//三级分类id
     *    brandId: 1,//品牌id
     *    status: 0,//商品状态
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        /**
         * status: 2
         * key:
         * brandId: 9
         * catelogId: 225
         */

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    @Transactional
    public void productUp(Long spuId) {
        //1、根据spuId查询sku相关信息
        List<SkuInfoEntity> skuEntity = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        List<Long> skuIds = skuEntity.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //查询当前sku所有可以别检索的规格属性
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.baseListforspu(spuId);
        List<Long> attrIds = attrValueEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        //查询能被检索的规格属性
        List<Long> Ids = attrService.selectAttrAndSearchByIds(attrIds);
        Set<Long> set = new HashSet<>(Ids);
        /**
         * todo 此处可以优化，这里只是要查询规格属性，且能被检索的，然后收集信息到SkuEsModel.Attrs
         *  那么都不需要进行filter，只需要查询的时候查询对应属性即可
         */
        //收集当前被检索的attr信息
        List<SkuEsModel.Attrs> AttrList = attrValueEntities.stream().filter(item -> {
            return set.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());
        //查询库存
        Map<Long,Boolean> stockMap = null;
        try{
            List<hasStockSkuVo> stockList = wareService.selectWareHasSkuStock(skuIds);
            stockMap = stockList.stream().collect(Collectors.toMap(hasStockSkuVo::getSkuId,item -> item.getStock()));
        }catch (Exception e){
            log.info("远程服务调用失败，原因{}",e);
        }

        //封装每个Sku基本信息
        Map<Long, Boolean> finalStockMap = stockMap;

        List<SkuEsModel> esModels = skuEntity.stream().map(item -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(item, skuEsModel);
            skuEsModel.setSkuPrice(item.getPrice());
            skuEsModel.setSkuImg(item.getSkuDefaultImg());
            //库存是否还有
            if(finalStockMap == null){
                skuEsModel.setHasStock(true);
            }else{
                skuEsModel.setHasStock(finalStockMap.get(item.getSkuId()));
            }
            //2、热度评分。0
            skuEsModel.setHotScore(0L);
            //3、查询品牌和分类的名字信息
            BrandEntity brand = brandService.getById(item.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            skuEsModel.setBrandId(brand.getBrandId());
            //分类
            CategoryEntity category = categoryService.getById(item.getCatalogId());
            skuEsModel.setCatalogName(category.getName());
            skuEsModel.setCatalogId(category.getCatId());
            //4、查询sku的规格属性：Attrs
            skuEsModel.setAttrs(AttrList);

            return skuEsModel;
        }).collect(Collectors.toList());

        //5、存储到es保存
        R r = searchService.saveSkuSearchInformation(esModels);
        if(r.getCode() == 0){
            //成功
            //5.1、修改商品状态为上架
            baseMapper.updateStatusById(spuId, productConstant.UpEnum.UP_STATUS.getCode());
        }else {
            //失败
            /**
             * todo 幂等性，失败还要继续调用
             *  feign内部也有重试机制，默认五次，不过默认不开启
             */

        }
    }

    @Override
    public List<SkuItemSaleAttrVo> querySkuBySpuId(Long spuId) {
        List<SkuItemSaleAttrVo>  saleAttrVo = baseMapper.selectSkuBySpuId(spuId);
        return saleAttrVo;
    }

    /**
     * 根据sku查询spu信息
     * @param skuId
     * @return
     */
    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfo = this.getById(skuInfo.getSpuId());
        return spuInfo;
    }
}
