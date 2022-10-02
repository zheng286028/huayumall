package com.zzl.huayumall.order.config;

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
 * @date 2022/09/08  16:24
 */
@Configuration
public class MySessionConfig {
    /**
     * 设置session的作用域
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setDomainName("huayumall.com");
        return defaultCookieSerializer;
    }

    /**
     * 设置redis的序列化
     * @return
     */
    @Bean
    public RedisSerializer redisSerializer(){
        return new GenericFastJsonRedisSerializer();
    }
}
