package com.zzl.huayumall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/14  15:30
 */
@Data
public class UserRegisterVo {
    private String userName;
    private String passWord;
    private String code;
    private String phone;
    private String email;
}
