package com.zzl.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/13  16:40
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.zzl.auth.feign"})
@EnableDiscoveryClient
@EnableRedisHttpSession
public class HuayumallAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(HuayumallAuthApplication.class,args);
    }
}
