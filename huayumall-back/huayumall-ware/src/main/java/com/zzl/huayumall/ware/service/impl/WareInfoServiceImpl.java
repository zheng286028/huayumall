package com.zzl.huayumall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zzl.common.utils.R;
import com.zzl.huayumall.ware.feign.MemberFeignService;
import com.zzl.huayumall.ware.vo.FreightAndMemberItemVo;
import com.zzl.huayumall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.ware.dao.WareInfoDao;
import com.zzl.huayumall.ware.entity.WareInfoEntity;
import com.zzl.huayumall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {
    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(item->{
                item.eq("id",key).or().like("name",key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),wrapper

        );

        return new PageUtils(page);
    }

    /**
     * 根据选中地址id获取运费
     * @param addrId
     * @return
     */
    @Override
    public FreightAndMemberItemVo getFreightByAddrId(Long addrId) {
        FreightAndMemberItemVo freightAndMemberItemVo = new FreightAndMemberItemVo();
        //1、模拟运费，远程查询用户信息
        R r = memberFeignService.getFeright(addrId);
        MemberAddressVo member = (MemberAddressVo) r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {});
        if(member!=null){
            //2、截取手机号最后一位模拟运费
            String phone = member.getPhone();
            //15338924359
            String substring = phone.substring(phone.length() - 1,phone.length());
            freightAndMemberItemVo.setAddress(member);
            freightAndMemberItemVo.setFreight(new BigDecimal(substring));
            return freightAndMemberItemVo;
        }
        return null;
    }

}
