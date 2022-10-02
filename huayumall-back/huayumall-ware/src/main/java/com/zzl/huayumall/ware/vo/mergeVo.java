package com.zzl.huayumall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/29  13:38
 */
@Data
public class mergeVo {
    @NotNull
    private Long purchaseId;

    private List<Long> items;
}
