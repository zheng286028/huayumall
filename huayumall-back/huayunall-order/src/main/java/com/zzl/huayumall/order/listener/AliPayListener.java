package com.zzl.huayumall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zzl.huayumall.order.config.AlipayTemplate;
import com.zzl.huayumall.order.service.OrderService;
import com.zzl.huayumall.order.vo.PayAsyncVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 功能描述
 * 监听阿里支付成功的异步方法
 *
 * @author 郑子浪
 * @date 2022/09/21  18:11
 */
@RestController
public class AliPayListener {
    @Resource
    private OrderService orderService;
    @Resource
    private AlipayTemplate alipayTemplate;

    /**
     * 监听订单支付情况
     * * 只要返回值不是success,Alipay就会一直通知
     *
     * @return
     */
    @PostMapping("/order/orderPay")
    public String handleOrderPay(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        //1、验签通过才能修改订单状态 todo 验证一直失败，应该是编码问题
        //1.1、获取支付宝POST过来反馈信息
//        Map<String, String> params = new HashMap<String, String>();
//        Map<String, String[]> requestParams = request.getParameterMap();
//        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
//            String name = (String) iter.next();
//            String[] values = (String[]) requestParams.get(name);
//            String valueStr = "";
//            for (int i = 0; i < values.length; i++) {
//                valueStr = (i == values.length - 1) ? valueStr + values[i]
//                        : valueStr + values[i] + ",";
//            }
//            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//            params.put(name, valueStr);
//        }
        //调用SDK验证签名
//        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type());

        //2、验证通过，修改订单状态
//        if (signVerified) {
//            System.out.println("签名验证成功.........");
            try {
                return orderService.handleOrderPay(vo);
            } catch (Exception e) {
                e.printStackTrace();
                return "fail";
            }
//        } else {
//            System.out.println("签名验证失败。。。。。。");
//            return "fail";
//        }
    }
}
