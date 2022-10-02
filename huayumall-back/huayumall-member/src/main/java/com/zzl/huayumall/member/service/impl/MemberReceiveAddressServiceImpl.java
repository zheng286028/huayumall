package com.zzl.huayumall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.member.dao.MemberReceiveAddressDao;
import com.zzl.huayumall.member.entity.MemberReceiveAddressEntity;
import com.zzl.huayumall.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据memberId查询会员地址信息
     * @param memberId
     * @return
     */
    @Override
    public List<MemberReceiveAddressEntity> getMemberAddress(Long memberId) {
        List<MemberReceiveAddressEntity> memberAddressItem = this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));
        if(memberAddressItem!=null && memberAddressItem.size()>0){
            return memberAddressItem;
        }
        return null;
    }

}
