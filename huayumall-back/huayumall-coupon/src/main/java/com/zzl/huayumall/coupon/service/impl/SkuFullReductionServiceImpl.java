package com.zzl.huayumall.coupon.service.impl;

import com.zzl.common.to.MemberPrice;
import com.zzl.common.to.SkuReductionTo;
import com.zzl.huayumall.coupon.entity.MemberPriceEntity;
import com.zzl.huayumall.coupon.entity.SkuLadderEntity;
import com.zzl.huayumall.coupon.service.MemberPriceService;
import com.zzl.huayumall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.coupon.dao.SkuFullReductionDao;
import com.zzl.huayumall.coupon.entity.SkuFullReductionEntity;
import com.zzl.huayumall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveInfo(SkuReductionTo skuReductionTo) {
        //5.4)、sku的优惠，满减信息 sms_sku_ladder\sms_sku_full_reduction\sms_member_price

        //sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
        if(skuLadderEntity.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }

        //sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(skuFullReductionEntity);
        }
        //sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        if(!memberPrice.isEmpty() && memberPrice.size()>0){
            //收集数据
            List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setMemberPrice(item.getPrice());
                memberPriceEntity.setMemberLevelId(item.getId());
                memberPriceEntity.setMemberLevelName(item.getName());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            }).filter(a->{
                return a.getMemberPrice().compareTo(new BigDecimal(0))==1;
            }).collect(Collectors.toList());
            //批量保存
            memberPriceService.saveBatch(collect);
        }
    }

}
