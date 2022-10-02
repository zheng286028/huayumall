package com.zzl.huayumall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述
 * 创建MQ的配置
 *
 * @author 郑子浪
 * @date 2022/09/17  14:17
 */
@Configuration
public class MyMQConfig {

    //订单交换机
    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order-event-exchange",true,false);
    }

    //订单发布队列
    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue",true,false,false);
    }

    //订单延时队列
    @Bean
    public Queue orderDelayQueue() {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000);
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }

    //绑定订单延时队列
    @Bean
    public Binding orderCreateBinding() {
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }
    //绑定订单发布队列
    @Bean
    public Binding orderReleaseBinding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }
    //订单关闭后绑定库存队列
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

}
