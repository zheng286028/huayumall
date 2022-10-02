package com.zzl.huayumall.ware.listener;

import com.rabbitmq.client.Channel;
import com.zzl.common.to.mq.OrderTo;
import com.zzl.common.to.mq.StockLockTo;
import com.zzl.huayumall.ware.service.WareSkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 功能描述
 * 监听库存的mq
 *
 * @author 郑子浪
 * @date 2022/09/18  20:44
 */
@RabbitListener(queues = "stock.release.stock.queue")
@Service
public class StockReleaseListener {
    @Resource
    private WareSkuService wareSkuService;

    /**
     * 自动解锁库存
     */
    @RabbitHandler
    public void handleStockLockRelease(StockLockTo stockLockTo, Message message, Channel channel) {
        try {
            wareSkuService.unlockStock(stockLockTo);
            //1、一切正常，接收消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                //2、发生异常将消息重新放回队列
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 订单关闭成功后解锁库存防止因为网络卡顿，导致库存先解锁
     */
    @RabbitHandler
    public void handleStockLockRelease(OrderTo orderTo, Message message, Channel channel) {
        System.out.println("订单关闭成功，开始解锁。。。。。:" + orderTo);
        try {
            wareSkuService.unlockStock(orderTo);
            //1、一切正常，接收消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                //2、发生异常将消息重新放回队列
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
