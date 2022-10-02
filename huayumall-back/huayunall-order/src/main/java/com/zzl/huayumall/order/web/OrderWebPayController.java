package com.zzl.huayumall.order.web;

import com.alipay.api.AlipayApiException;
import com.zzl.huayumall.order.config.AlipayTemplate;
import com.zzl.huayumall.order.service.OrderService;
import com.zzl.huayumall.order.vo.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/20  13:54
 */
@Controller
public class OrderWebPayController {
    @Resource
    private OrderService orderService;
    @Resource
    private AlipayTemplate alipayTemplate;
    /**
     * 支付包支付
     * @param orderSn
     * @return
     */
    @GetMapping(value = "/payOrder/{orderSn}",produces = "text/html")
    @ResponseBody
    public String payOrder(@PathVariable("orderSn")String orderSn) throws AlipayApiException {
        //1、封装支付所需数据
        PayVo payVo = orderService.getOrderPayItemByOrderSn(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }
}
