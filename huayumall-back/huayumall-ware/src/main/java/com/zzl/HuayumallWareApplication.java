package com.zzl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableFeignClients(basePackages = {"com.zzl.huayumall.ware.feign"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableRabbit
public class HuayumallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuayumallWareApplication.class, args);
    }

}
