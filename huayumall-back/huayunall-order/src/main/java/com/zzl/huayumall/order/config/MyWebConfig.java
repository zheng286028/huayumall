package com.zzl.huayumall.order.config;

import com.zzl.huayumall.order.interceptor.UserLongInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 功能描述
 *  web的相关功能配置
 * @author 郑子浪
 * @date 2022/09/08  18:57
 */
@Controller
public class MyWebConfig implements WebMvcConfigurer {
    @Autowired
    private UserLongInterceptor user;

    /**
     * 实现登录拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(user).addPathPatterns("/**");
    }
}
