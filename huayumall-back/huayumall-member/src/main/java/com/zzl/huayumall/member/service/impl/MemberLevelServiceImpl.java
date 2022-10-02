package com.zzl.huayumall.member.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.utils.PageUtils;
import com.zzl.common.utils.Query;

import com.zzl.huayumall.member.dao.MemberLevelDao;
import com.zzl.huayumall.member.entity.MemberLevelEntity;
import com.zzl.huayumall.member.service.MemberLevelService;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询默认会员
     * @return
     */
    @Override
    public MemberLevelEntity selectDefaultVipGrade() {
        MemberLevelEntity entity = baseMapper.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        return entity;
    }

}
