package com.zzl.common.to.mq;

import lombok.Data;
import lombok.ToString;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/17  19:45
 */
@ToString
@Data
public class StockLockTo {
    //工作订单id
    private Long id;
    //工作订单详情id
    private StockDetailTo stockDetailTo;
}
