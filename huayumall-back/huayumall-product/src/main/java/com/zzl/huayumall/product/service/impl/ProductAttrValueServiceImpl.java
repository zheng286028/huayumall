package com.zzl.huayumall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.product.dao.ProductAttrValueDao;
import com.zzl.huayumall.product.entity.ProductAttrValueEntity;
import com.zzl.huayumall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBeanProductAttrValue(List<ProductAttrValueEntity> collect) {
        if(!collect.isEmpty() || collect.size()>0){
            this.saveBatch(collect);
        }
    }

    /**
     * 根据spuId查询规格属性
     * @param id
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> baseListforspu(Long id) {
        List<ProductAttrValueEntity> ProductAttrValueList = this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",id));
        return ProductAttrValueList;
    }

    /**
     * 根据spuId修改
     * @param spuId
     * @param valueEntities
     */
    @Override
    @Transactional
    public void updateByspuId(Long spuId, List<ProductAttrValueEntity> valueEntities) {
        //有些数据是原来没有的，所有先删除旧数据在新增
        baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        //新增，先设置id
        List<ProductAttrValueEntity> collect = valueEntities.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        //新增
        this.saveBatch(valueEntities);
    }

}
