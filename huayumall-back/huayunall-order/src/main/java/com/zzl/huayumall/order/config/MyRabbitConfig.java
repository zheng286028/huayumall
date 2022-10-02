package com.zzl.huayumall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/07  15:46
 */
@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 转发消息是使用JSON来构建
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 消息确认回调
     * @PostConstruct：表示创建MyRabbitConfig这个类后的回调方法
     */
    @PostConstruct
    public void initRabbit(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * @param correlationData
             * @param ack :消息成功发送到服务器：true
             * @param cause ：发生错误的信息
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("消息发送的回调开始执行，消息的ID为："+correlationData+"====:消息发送情况="+ack+"====:发送错误："+cause);
            }
        });
        /**
         * 消息投递到队列失败的回调
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             *
             * @param message：投递的消息
             * @param replyCode ：回复的代码
             * @param replyText ：回复的文本内容
             * @param exchange ： 投递的交换机
             * @param routingKey ：投递的路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("投递失败的消息：{"+message+"}=====>回复的代码{"+replyCode+"}====>回复的文本内容{"+replyText+"}====>投递的交换机{"+exchange+"}====>投递的路由键{"+routingKey+"}");

            }
        });
    }
}
