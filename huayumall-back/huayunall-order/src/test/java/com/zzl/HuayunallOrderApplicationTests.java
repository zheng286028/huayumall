package com.zzl;

import com.zzl.huayumall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class HuayunallOrderApplicationTests {
    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage1(){
        System.out.println("你好");
    }

    @Test
    public void sendMessage(){

        for (int i = 0; i < 10; i++) {
            OrderReturnReasonEntity order = new OrderReturnReasonEntity();
            order.setId(1L);
            order.setName("王五");
            order.setCreateTime(new Date());
            String msg = "你好呀";
            rabbitTemplate.convertAndSend("java-exchange","huayumall",order);
            log.info("消息发送成功，信息为：{}",order);
        }
    }

    @Test
    public void createExchange() {
        /**
         * DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
         * name:交换机名称
         * durable：是否持久
         * autoDelete：是否自动删除
         * arguments：自定义参数
         */
        DirectExchange directExchange = new DirectExchange("java-exchange",true,false);
            amqpAdmin.declareExchange(directExchange);
            log.info("[{}]交换机创建成功","java-exchange");
    }

    @Test
    public void createQueue(){
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
         * name:队列名称
         * durable：是否持久化
         * exclusive：是否排他
         *              排他只能一个客户端连接该队列
         *  autoDelete：是否自动删除
         */
        Queue queue = new Queue("java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("[{}]队列创建成功","java-queue");
    }

    @Test
    public void createBinding(){
        /**
         * String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
         * destination:目的地
         * destinationType：目的地类型
         * exchange：交换机
         * routingKey：路由键
         */
        Binding binding = new Binding("java-queue",Binding.DestinationType.QUEUE,"java-exchange","huayumall",null);
        amqpAdmin.declareBinding(binding);
        log.info("路由键为[{}]绑定成功","huayumall");
    }
}
