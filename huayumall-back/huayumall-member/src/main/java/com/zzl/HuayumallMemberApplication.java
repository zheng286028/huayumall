package com.zzl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = ("com/zzl/huayumall/member/feign"))
@SpringBootApplication
@EnableRedisHttpSession
@MapperScan({"com/zzl/huayumall/member/dao"})
public class HuayumallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuayumallMemberApplication.class, args);
    }

}
