package com.zzl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan({"com/zzl/huayumall/coupon/dao"})
public class HuayumallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuayumallCouponApplication.class, args);
    }

}
