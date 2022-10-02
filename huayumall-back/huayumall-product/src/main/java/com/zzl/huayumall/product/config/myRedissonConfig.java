package com.zzl.huayumall.product.config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/03  22:41
 */
@Configuration
public class myRedissonConfig {

    @Bean
    public RedissonClient client(){

        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
