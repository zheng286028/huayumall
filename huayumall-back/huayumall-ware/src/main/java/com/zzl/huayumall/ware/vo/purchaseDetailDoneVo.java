package com.zzl.huayumall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/29  18:58
 */
@Data
public class purchaseDetailDoneVo {
    //itemId:1,status:4,reason:""
    @NotNull
    private Long itemId;
    private Integer status;
    private String reason;
}
