package com.zzl.huayumall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.zzl.common.Valid.AddGroup;
import com.zzl.common.Valid.ListValue;
import com.zzl.common.Valid.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author zhengzilang
 * @email 2860285053@qq.com
 * @date 2022-07-19 22:44:56
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(groups = {AddGroup.class},message = "不能携带id")
	@NotNull(groups = {UpdateGroup.class},message = "id不能为空")
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo地址必须是一个合法的地址",groups = {AddGroup.class,UpdateGroup.class})
	@NotEmpty(message = "logo不能为空",groups = {AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "描述不能为空",groups = {AddGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(vale = {0,1},groups = {AddGroup.class,UpdateGroup.class})
	@NotNull(groups = AddGroup.class)
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z{1}]$",message = "检索字符必须是一个字母",groups = {AddGroup.class,UpdateGroup.class})
	@NotEmpty(message = "检索字母不能为空",groups = {AddGroup.class})
	private String firstLetter;

	/**
	 * 排序
	 */
	@DecimalMin(value = "0",message = "状态的值只能是大于等于0",groups = {AddGroup.class,UpdateGroup.class})
	@NotNull(groups = {AddGroup.class})
	private Integer sort;

}
