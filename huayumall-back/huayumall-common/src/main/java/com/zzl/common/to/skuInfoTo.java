package com.zzl.common.to;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * ��������
 *
 * @author ֣����
 * @date 2022/07/29  20:19
 */
@Data
public class skuInfoTo {
    /**
     * skuId
     */
    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * sku����
     */
    private String skuName;
    /**
     * sku��������
     */
    private String skuDesc;
    /**
     * ��������id
     */
    private Long catalogId;
    /**
     * Ʒ��id
     */
    private Long brandId;
    /**
     * Ĭ��ͼƬ
     */
    private String skuDefaultImg;
    /**
     * ����
     */
    private String skuTitle;
    /**
     * ������
     */
    private String skuSubtitle;
    /**
     * �۸�
     */
    private BigDecimal price;
    /**
     * ����
     */
    private Long saleCount;
}
