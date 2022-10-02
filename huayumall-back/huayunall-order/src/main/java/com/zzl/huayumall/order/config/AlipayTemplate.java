package com.zzl.huayumall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.zzl.huayumall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000121667567";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCsxLbMBZ7zIiK6WbkVN4SichTfo3mC8M9MjYfWN9xvmCbemE0DiSgMoaAWYpheX42S5lLK9gKmLh+ALR2iDWNxDJsJjAlTEJr7V/a0n+YmQIGQqq+FqcoKamNrGB9zDNWjMfSd8U9T3senIzKrPcBNV3dhySD8vR1i4leIyY67rqXChIG3yskwFdKS+rIN0TE53VECWXA79OGYbt8WFRgJ83OeLVAno++tMUst7XILGb6sUw+rNKjlWRudxRYcylcWLfgKG8fwULA2SsZpz0PLvsA/ES9YjJr18NP/ydDFrP2EmFdiY6LDSa6+OPAVRcapuhieVxs17nNSPVxgFjV9AgMBAAECggEAG+TCspvxtsV7H+Ueibtl8ahDkHvfQXHJtk1fcgmg0G+bTvL99CUvzyJFK3gNq6b3v5DqKxSfBt+JfHNMRn6s4HKtkVQHqV6T9yKTlUP/r4tYn9e+8PChyx60IHVLlpKq52dIXDu6mAru5yA6rQUSSCpn/uSvK78IwI9UnKQWJFzhQ184OXZGhjmWsaZfAJifhMi4A1n3JQD4qcNeE9hCRwMdPuXHjf0RhoussiHWAF3dNyjBGiUsHDqDrVxql7Ea/QsvDyHx9G4XOqfvCGut5dAX0/44Yma46IzyjDz+FbkOb/gP0Ri9NrWbnT6fiJsI72L5GgX5FHYdttnGS05s7QKBgQDTPYOB5jo8CsedEZsYgVF6EGXBzIPBEqs8HeQXpqeqY12IOt6v6rgRPI/R1gpwcmVNyXTPIz64U8CBZdbhS2tVNQS4yAHbcqzd1JVyqdFj1eNO/iPiPA23Mil4Xah1gT0cYHJObYLXs2rn3zZN/GFQ4I7+sqZ8bAwFF3f1WM0DrwKBgQDRYFZ80GpZTzUStUr4cJBC+Ja6AiHJmz115RfV8lvibhtL5hOnSLzACXxOQgbDHzRLNxCqrIkuG/8j4xpcr/2DEtPve159BL1nR5MTVg1BNahmrbpEKyejNS5TmKhBlHG234V8mf2mRpohN1y2xyBHkIHsPeyxNtOkw6zE5oJokwKBgQCtP4g5447ja5vncGcZpAYk7Inodkmg8iTKe2F1HFPq5O/zKQU2lnU8fcUevkN1kf0P3SgWMrvgBaCe/91KjcaL7Z3fxvnu95Bbp1yi3DBFh0iUos7fX42IMHXeqEHfjY2HVnqpZBupuRb28RTixRkzcdkVf4QYC62YB3V1Hc2XNQKBgGxXZWmAjwdXrSbZGMoltQBykcv+fm1MHyl6OjNDDbjFMOq9Sz4srT7Rqbj1AXrGEtE41S77yKBLbGKtiV6c0pmYjIJHXEVkxn1C1CAVTxaHJXS8QL41dnVFd4taWvZUUeNHFlO7ez0nG2pEs30A3PsrE4+jG28355xChIv+m0xxAoGALT73sJ1yp0g2wEeGr8MhCXSKeHdBkYrEz2pdVm/CskLU/74mI77J7Mujd5rikqzCn/b7DvYH+8DgpF3t6hgtXkLOeNBHKHRj/b3FbB5nR9unB4FUC9pWYgYhtmCkp4ETbEid9jICI8hdiQot4bfkxZEH3fosAG4/pzJi156Hwjs=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqpXFn0hxgHhNVdc/1m/5hSmp8TsXX0hkUm2knZazkMF7Jo1vaJMYuwtQOtEYSWWQVkNrkJKXHJFuRTeaaW7AylrKXV5l4JrYb9yKOxOdTyZe2t5SSb//WfGPecf0/L8ts8ML1/IseBOFL9/6Ivy0ckPPq0pjWcvQVNRuqGgh+1yAyTQo7faCTcSe6r3YYJSf13I40kWTTqnWbpfI6DravY4LLvrV92O2MvrK9mpk8Ib9UtqaiUcfCPMOJgwNvVVdyEwB2ooSC0QAf5pOsbhCRny1Fs2hBGPNk3XnWZ95pNHES0hubeijjBfrJsJNsvjU48iTRsHP7qgBA2qsMDnfsQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息，可以在此修改订单状态
    private  String notify_url = "https://9ac9-36-101-114-60.jp.ngrok.io/order/orderPay";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.huayumall.com/OrderPage";
    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    //订单超时时间
    private String timeOut = "1m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+ timeOut +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }

}
