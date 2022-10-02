package com.zzl.huayumall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/13  15:59
 */
@ConfigurationProperties(prefix = "huayumall.thread")
@Component
@Data
public class ThreadPoolExecutorProperties {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
}
