package com.zzl.auth.vo;

import lombok.Data;

/**
 * 功能描述
 *  社交登录所需信息
 * @author 郑子浪
 * @date 2022/08/16  21:08
 */
@Data
public class SocialLoginVo {
    /**
     * Copyright 2022 json.cn
     */
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;
    private String scope;
    private long createdAt;

}
