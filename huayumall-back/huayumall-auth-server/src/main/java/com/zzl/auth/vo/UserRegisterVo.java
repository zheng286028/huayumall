package com.zzl.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/14  13:57
 */
@Data
public class UserRegisterVo {

    @NotNull
    @Length(min = 6,max = 15,message = "账号长度不符合")
    private String userName;

    @NotNull
    @Length(min = 6,max = 15,message = "密码长度不符合")
    private String passWord;

    @NotNull
    @Length(min = 6,max = 6,message = "验证码错误")
    private String code;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",message = "邮箱号格式有误")
    private String email;
}
