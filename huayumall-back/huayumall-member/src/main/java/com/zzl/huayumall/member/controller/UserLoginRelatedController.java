package com.zzl.huayumall.member.controller;

import com.zzl.common.exception.BizCodeEnum;
import com.zzl.common.utils.R;
import com.zzl.huayumall.member.entity.MemberEntity;
import com.zzl.huayumall.member.exception.PhoneExistException;
import com.zzl.huayumall.member.exception.UserNameExistException;
import com.zzl.huayumall.member.service.MemberService;
import com.zzl.huayumall.member.vo.SocialLoginVo;
import com.zzl.huayumall.member.vo.UserLoginVo;
import com.zzl.huayumall.member.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/14  15:28
 */
@Slf4j
@Controller
public class UserLoginRelatedController {
    @Resource
    private MemberService memberService;

    /**
     * 注册用户
     * @param vo
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public R userRegister(@RequestBody UserRegisterVo vo){
        try {
            memberService.saveUserRegister(vo);
        }catch (PhoneExistException ex){
            return R.error(BizCodeEnum.USER_PHONE_EXIST_EXCEPTION.getCode(),BizCodeEnum.USER_PHONE_EXIST_EXCEPTION.getMsg());
        }catch (UserNameExistException ex){
            return R.error(BizCodeEnum.USER_USERNAME_EXIST_EXCEPTION.getCode(),BizCodeEnum.USER_USERNAME_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 登录验证
     * @param vo
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public R userLoginVerify(@RequestBody UserLoginVo vo){
       MemberEntity entity =  memberService.userLoginVerify(vo);
       if(entity == null){
           return R.error("账号或密码错误");
       }
       return R.ok().put("user",entity);
    }

    /**
     * 社交登录或注册
     * @param socialUserVo
     * @return
     */
    @PostMapping("/social/login")
    @ResponseBody
    public R socialUserRegisterOrLogin(@RequestBody SocialLoginVo socialUserVo) {
        MemberEntity memberEntity = null;
        try {
            memberEntity = memberService.socialUserRegisterOrLogin(socialUserVo);
            if (memberEntity == null) {
                //报错
                return R.error("系统繁忙，请稍后重试");
            }
        } catch (Exception e) {
            log.error("异常报错");
            R.error("系统繁忙，请稍后重试");
        }
        return R.ok().put("data",memberEntity);
    }
}
