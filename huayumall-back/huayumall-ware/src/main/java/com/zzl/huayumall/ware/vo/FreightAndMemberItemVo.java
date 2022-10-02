package com.zzl.huayumall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/10  17:07
 */
@Data
public class FreightAndMemberItemVo {
    private MemberAddressVo address;
    private BigDecimal freight;
}
