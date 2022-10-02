package com.zzl.huayumall.ware.vo;

import lombok.Data;

/**
 * 功能描述
 *  库存锁定结果
 * @author 郑子浪
 * @date 2022/09/11  21:00
 */
@Data
public class WareLockResultVo {
    //锁定库存的商品
    private Long skuId;
    //锁定了多少
    private Integer num;
    //锁定成功与否
    private Boolean locked;
}
