package com.zzl.search.vo;

import lombok.Data;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/03  20:02
 */
@Data
public class UserInfo {
    //用户id
    private Long userId;
    //浏览器的cookie
    private String userKey;
    //是否已经分配user-key
    private Boolean isTempUser = false;
}
