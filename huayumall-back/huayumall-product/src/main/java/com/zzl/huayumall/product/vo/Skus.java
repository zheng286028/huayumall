/**
  * Copyright 2019 bejson.com
  */
package com.zzl.huayumall.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2019-11-26 10:50:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Skus {

    //sms_sku_ladder
    private int fullCount;
    private BigDecimal discount;
    private BigDecimal price;
    //sms_sku_full_reduction
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    //sms_member_price
    private List<Attr> attr;
    private String skuName;
    private String skuTitle;
    private String skuSubtitle;
    private List<Images> images;
    private List<String> descar;
    private int countStatus;
    private int priceStatus;
    private List<MemberPrice> memberPrice;


}
