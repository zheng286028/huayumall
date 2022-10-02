package com.zzl.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.zzl.auth.feign.MemberFeignService;
import com.zzl.auth.service.SendEmailCodeService;
import com.zzl.auth.utils.VerificationCode;
import com.zzl.auth.vo.UserLoginVo;
import com.zzl.auth.vo.UserRegisterVo;
import com.zzl.common.constant.AuthServerConstant;
import com.zzl.common.exception.BizCodeEnum;
import com.zzl.common.utils.R;
import com.zzl.common.vo.UserItemVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/13  17:03
 */
@Controller
@Slf4j
public class LoginController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MemberFeignService feignService;
    @Resource
    private SendEmailCodeService sendEmailCodeService;

    /**
     * 接收存储验证码
     * @return
     */
    @PostMapping("/sms/sendcode")
    @ResponseBody
    public R verificationCodeCheck(String email){
        String codeStr = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + email);
        if(StringUtils.isNotEmpty(codeStr)){
            long l = Long.parseLong(codeStr.split("_")[1]);
            if(System.currentTimeMillis() - l <60000){
                //当前手机已经发过验证码
                return R.error(BizCodeEnum.SMS_CODE.getCode(),BizCodeEnum.SMS_CODE.getMsg());
            }
        }
        //2、发送验证码到邮箱
        Integer code = VerificationCode.generateValidateCode(6);
        //3、发送的内容
        String sendText = "【华宇商城】 你好，你本次登录验证码为："+code+","+"请你尽快登录，验证码有效期为：三分钟";
        sendEmailCodeService.sendCodeEmail(email,sendText);
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+email,code+"_"+System.currentTimeMillis(),180, TimeUnit.SECONDS);
        log.info("code:{}",code);
        return R.ok();
    }

    /**
     * 注册验证
     * @param
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo,BindingResult result,RedirectAttributes redirectAttributes){
        //判断校验是否通过
        if(result.hasErrors()){
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("error",collect);
            return "redirect:http://auth.huayumall.com/reg.html";
        }
        //验证码校验
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getEmail());
        if(StringUtils.isNotEmpty(s) && vo.getCode().equals(s.split("_")[0])){
            //删除验证码
            stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getEmail());
            //调用远程服务，完成注册
            R r = feignService.userRegister(vo);
            if(r.getCode()!=0){
                //注册失败，回到注册页面，带上提示信息
                HashMap<String, String> map = new HashMap<>();
                map.put("msg", (String) r.getData("msg",new TypeReference<String>(){}));
                redirectAttributes.addFlashAttribute("error",map);
                return "redirect:http://auth.huayumall.com/reg.html";
            }
            return "redirect:http://auth.huayumall.com/login.html";
        }else{
            Map<String, String> collect = new HashMap<>();
            collect.put("code","验证码有误");
            redirectAttributes.addFlashAttribute("error",collect);
            return "redirect:http://auth.huayumall.com/reg.html";
        }
    }
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.USER_LOGIN_SUCCESS);
        log.info("用户信息登录信息：{}",attribute);
        if(attribute==null){
            //没登录
            return "login";
        }
        return "redirect:http://huayumall.com";
    }

    /**
     * 登录验证
     * @param vo
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/login")
    public String Login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session){
        //调用远程服务进行登录验证
        R r = feignService.userLoginVerify(vo);
        if(r.getCode()!=0){
            Map<String, String> collect = new HashMap<>();
            collect.put("msg", (String) r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("error",collect);
            //登录失败
            return "redirect:http://auth.huayumall.com/reg.html";
        }
        //存储该用户信息
        UserItemVo user = (UserItemVo) r.getData("user", new TypeReference<UserItemVo>() {
        });
        session.setAttribute(AuthServerConstant.USER_LOGIN_SUCCESS,user);
        return "redirect:http://huayumall.com";
    }
}
