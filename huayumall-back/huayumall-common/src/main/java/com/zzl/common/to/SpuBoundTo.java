package com.zzl.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ��������
 *
 * @author ֣����
 * @date 2022/07/27  17:31
 */
@Data
public class SpuBoundTo {
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
    private Long SpuId;
}
