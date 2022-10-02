package com.zzl.huayumall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/09  14:27
 */
@Configuration
public class MyFeignConfig {
    /**
     * feign在构造时会获取很多请求拦截器：RequestInterceptor(也就是feign在构造时会调用该方法)
     * RequestTemplate：feign远程调用新的请求模板
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //1、获取浏览器的请求数据，原理就是spring在共享线程里存储了httpServletRequest
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes!=null){
                    HttpServletRequest request = attributes.getRequest();
                    //2、获取cookie
                    String cookie = request.getHeader("cookie");
                    //3、设置到信息的请求模板里
                    template.header("cookie",cookie);
                }
            }
        };
    }
}
