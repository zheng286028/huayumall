package com.zzl.huayumall.ware.exception;

import lombok.Data;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/11  21:52
 */
@Data
public class NoStockException extends RuntimeException{
    private Long skuId;

    public NoStockException(Long skuId){
        super("商品id为："+skuId+"没有库存");
    }
}
