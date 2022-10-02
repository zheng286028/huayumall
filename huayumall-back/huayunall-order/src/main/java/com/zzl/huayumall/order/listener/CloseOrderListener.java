package com.zzl.huayumall.order.listener;

import com.rabbitmq.client.Channel;
import com.zzl.huayumall.order.entity.OrderEntity;
import com.zzl.huayumall.order.enume.OrderStatusEnum;
import com.zzl.huayumall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/18  22:39
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class CloseOrderListener {
    @Resource
    private OrderService orderService;

    @RabbitHandler
    public void listenQueue(OrderEntity order, Channel channel, Message message) throws IOException {
        try {
            //1、查询该订单状态
            OrderEntity orderEntity = orderService.getById(order.getId());
            if(orderEntity.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())){
                //1.2、可以修改订单状态
                orderService.closeOrder(order);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }catch (Exception e){
            //2、拒收消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }

    }
}
