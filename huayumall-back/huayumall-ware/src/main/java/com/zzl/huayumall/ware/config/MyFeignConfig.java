package com.zzl.huayumall.ware.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/20  19:18
 */
@Configuration
public class MyFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(requestAttributes!=null){
                    HttpServletRequest request = requestAttributes.getRequest();
                    //2、获取cookie
                    String cookie = request.getHeader("cookie");
                    //3、设置到信息的请求模板里
                    template.header("cookie",cookie);
                }
            }
        };
    }
}
