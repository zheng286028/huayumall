package com.zzl.huayumall.order.controller;

import com.zzl.huayumall.order.entity.OrderEntity;
import com.zzl.huayumall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;

import java.util.Date;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/07  17:50
 */
@RestController
public class RabbitController {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/send")
    public String sendMessage(@RequestParam(value = "num") int num){
        for (int i = 0; i < num; i++) {
                OrderReturnReasonEntity order = new OrderReturnReasonEntity();
                order.setId(1L);
                order.setName("王五");
                order.setCreateTime(new Date());
                String msg = "你好呀";
                rabbitTemplate.convertAndSend("java-exchange","huayumall",order);
        }
        return "ok";
    }
}
