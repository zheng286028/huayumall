package com.zzl.huayumall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.SkuSaleAttrValueDao;
import com.zzl.huayumall.product.entity.SkuSaleAttrValueEntity;
import com.zzl.huayumall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBeanListSkuSaleAttrValue(List<SkuSaleAttrValueEntity> skuSaleAttrValueList) {
        if(!skuSaleAttrValueList.isEmpty() && skuSaleAttrValueList.size()>0){
            this.saveBatch(skuSaleAttrValueList);
        }
    }

    /**
     * 查询sku对应的销售属性Name和Value
     * @param skuId
     * @return
     */
    @Override
    public List<String> getSkuAttrBySkuId(Long skuId) {
        return baseMapper.selectSkuAttrBySkuId(skuId);
    }

}
