package com.zzl.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * ¹¦ÄÜÃèÊö
 *
 * @author Ö£×ÓÀË
 * @date 2022/07/27  18:05
 */
@Data
public class SkuReductionTo{
    private Long skuId;
    //sms_sku_ladder
    private int fullCount;
    private BigDecimal discount;
    //sms_sku_full_reduction
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private int countStatus;
    private List<MemberPrice> memberPrice;
}
