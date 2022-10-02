package com.zzl.common.to.mq;

import lombok.Data;

/**
 * ��������
 *
 * @author ֣����
 * @date 2022/09/17  19:54
 */
@Data
public class StockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * �������
     */
    private Integer skuNum;
    /**
     * ������id
     */
    private Long taskId;
    /**
     * �ֿ�id
     */
    private Long wareId;
    /**
     * 1-������  2-�ѽ���  3-�ۼ�
     */
    private Integer lockStatus;

}
