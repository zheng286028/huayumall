package com.zzl.huayumall.order.web;

import com.rabbitmq.client.Channel;
import com.zzl.huayumall.order.entity.OrderEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.UUID;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/08  16:15
 */
@Controller
public class HelloController {

    /**
     * 动态跳转订单页面
     * @param page
     * @return
     */
    @GetMapping("/{page}.html")
    public String index(@PathVariable("page") String page){
        return page;
    }
}
