package com.zzl.cart.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/20  22:45
 */
@Configuration
public class RedisSessionConfig {
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setDomainName("huayumall.com");
        return serializer;
    }
    @Bean
    public RedisSerializer redisSerializer(){
        return new GenericFastJsonRedisSerializer();
    }
}

