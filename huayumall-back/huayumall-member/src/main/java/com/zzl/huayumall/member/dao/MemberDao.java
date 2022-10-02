package com.zzl.huayumall.member.dao;

import com.zzl.huayumall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.huayumall.member.vo.UserLoginVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:40:26
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    MemberEntity userLoginVerify(String userName);
}
