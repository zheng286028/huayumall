package com.zzl.huayumall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/11  16:07
 */
@Data
public class SubmitOrderVo {
    //地址id
    private Long addrId;
    //支付方式
    private Integer payType;
    //应付价格
    private BigDecimal payPrice;
    //防重令牌
    private String orderToken;
}
