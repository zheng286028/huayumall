package com.zzl.huayumall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.zzl.huayumall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/07/27  12:46
 */
@Data
public class AttrGroupWithAttrVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
