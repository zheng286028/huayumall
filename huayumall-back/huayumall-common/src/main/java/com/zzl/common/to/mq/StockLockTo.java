package com.zzl.common.to.mq;

import lombok.Data;
import lombok.ToString;

/**
 * ��������
 *
 * @author ֣����
 * @date 2022/09/17  19:45
 */
@ToString
@Data
public class StockLockTo {
    //��������id
    private Long id;
    //������������id
    private StockDetailTo stockDetailTo;
}
