package com.zzl;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class HuayumallCouponApplicationTests {

    @Test
    public void contextLoads() {
        LocalDate now = LocalDate.now();
        LocalDate days = now.plusDays(2);
        LocalTime max = LocalTime.MAX;
        String format = LocalDateTime.of(days, max).format(DateTimeFormatter.ofPattern(" yyyy-MM-dd HH:mm:ss"));
        System.out.println(format);
    }

}
