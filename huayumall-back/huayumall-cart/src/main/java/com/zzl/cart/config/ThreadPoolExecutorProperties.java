package com.zzl.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 功能描述
 *  指定线程池基本配置
 * @author 郑子浪
 * @date 2022/09/04  17:23
 */
@ConfigurationProperties(prefix = "huayumall.thread")
@Component
@Data
public class ThreadPoolExecutorProperties {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
}
