package com.zzl.huayumall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.utils.PageUtils;
import com.zzl.huayumall.member.entity.MemberEntity;
import com.zzl.huayumall.member.exception.PhoneExistException;
import com.zzl.huayumall.member.exception.UserNameExistException;
import com.zzl.huayumall.member.vo.SocialLoginVo;
import com.zzl.huayumall.member.vo.UserLoginVo;
import com.zzl.huayumall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-20 00:40:26
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveUserRegister(UserRegisterVo vo) throws PhoneExistException, UserNameExistException;

    MemberEntity userLoginVerify(UserLoginVo vo);

    MemberEntity socialUserRegisterOrLogin(SocialLoginVo socialUserVo) throws Exception;
}

