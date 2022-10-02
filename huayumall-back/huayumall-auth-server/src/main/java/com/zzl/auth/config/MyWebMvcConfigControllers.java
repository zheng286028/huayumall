package com.zzl.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 功能描述
 *  页面跳转
 * @author 郑子浪
 * @date 2022/08/13  19:36
 */
@Configuration
public class MyWebMvcConfigControllers implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
