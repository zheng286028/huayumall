package com.zzl.huayumall.seckill;

import org.apache.tomcat.jni.Local;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//@SpringBootTest
public class HuayumallSeckillApplicationTests {

    @Test
    public void contextLoads() {
        //1、最近三天
        LocalDate now = LocalDate.now();
        LocalDate days = now.plusDays(1);
        LocalDate days2 = now.plusDays(2);

        //2、最小时间和最大时间
        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;

        //3、结合
        LocalDateTime startDate = LocalDateTime.of(now, min);
        LocalDateTime endDate = LocalDateTime.of(days2, max);
    }

}
