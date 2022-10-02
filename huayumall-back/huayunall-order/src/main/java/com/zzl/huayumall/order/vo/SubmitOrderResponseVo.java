package com.zzl.huayumall.order.vo;

import com.zzl.huayumall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/11  16:11
 */
@Data
public class SubmitOrderResponseVo {
    private OrderCreateVo order;
    //结果码，0：成功
    private int code;
}
