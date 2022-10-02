package com.zzl.huayumall.member.config;

import com.zzl.huayumall.member.interceptor.UserLongInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/20  19:00
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Resource
    private UserLongInterceptor userLongInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLongInterceptor).addPathPatterns("/**");
    }
}
