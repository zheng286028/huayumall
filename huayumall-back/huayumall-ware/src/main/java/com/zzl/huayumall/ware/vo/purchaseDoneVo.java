package com.zzl.huayumall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/29  18:56
 */
@Data
public class purchaseDoneVo {
    @NotNull
    private Long id;

    private List<purchaseDetailDoneVo> items;
}
