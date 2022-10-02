package com.zzl.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zzl.auth.feign.MemberFeignService;
import com.zzl.auth.vo.SocialLoginVo;
import com.zzl.common.constant.AuthServerConstant;
import com.zzl.common.vo.UserItemVo;
import com.zzl.common.utils.HttpUtils;
import com.zzl.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * 功能描述
 * 处理社交登录请求
 *
 * @author 郑子浪
 * @date 2022/08/14  23:14
 */
@Slf4j
@Controller
public class OAuth2Controller {
    @Resource
    MemberFeignService memberFeignService;

    /**
     * 完成gitee的社交登录
     * @param code
     * @param session
     * @return
     * @throws Exception
     */
    @GetMapping("/auth2.0/gitee/success")
    public String oAuthGitee(String code, HttpSession session) throws Exception {
        //1、根据code码换取accessToken
        HashMap<String, String> map = new HashMap<>();
        //封装gitee的社交登录信息
        map.put("grant_type", "authorization_code");
        map.put("client_id", "298840215033b5b2f1bb104e2b2f043c6ff77e99b624b590cd4a44268e96e3f4");
        map.put("redirect_uri", "http://auth.huayumall.com/auth2.0/gitee/success");
        map.put("client_secret", "247ea07eb804a5d93b3625390fd2d8f38cd41add7b194d685807dfb877909169");
        map.put("code", code);
        //发送post请求换取accessToken
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), new HashMap<>(), map);
        if (response.getStatusLine().getStatusCode() == 200) {
            //发送成功，换取accessToken码
            HttpEntity entity = response.getEntity();
            String string = EntityUtils.toString(entity);
            SocialLoginVo socialUserVo = JSON.parseObject(string, SocialLoginVo.class);
            //调用远程信息进行注册
            R r = memberFeignService.socialUserRegisterOrLogin(socialUserVo);
            if (r.getCode() != 0) {
                //失败，返回登录页面
                return "redirect:http://auth.huayumall.com/login.html";
            }
            //2、获取该用户信息，返回页面，使用SpringSession来完成session共享
            UserItemVo data = (UserItemVo) r.getData("data", new TypeReference<UserItemVo>() {
            });
            log.info("用户信息为：{}",data);
            //将登录成功的用户存储到session里
            session.setAttribute(AuthServerConstant.USER_LOGIN_SUCCESS,data);
            //3、登录成功回到首页
            return "redirect:http://huayumall.com";
        } else {
            //登录失败回到登录页 ，给出失败的提示
            return "redirect:http://auth.huayumall.com/login.html";
        }

    }
}
