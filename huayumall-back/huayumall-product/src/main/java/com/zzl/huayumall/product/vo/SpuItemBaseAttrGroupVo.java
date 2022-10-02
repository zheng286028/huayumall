package com.zzl.huayumall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/11  19:33
 */
@Data
public class SpuItemBaseAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
