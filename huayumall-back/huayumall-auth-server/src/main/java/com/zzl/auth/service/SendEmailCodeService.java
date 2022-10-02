package com.zzl.auth.service;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/29  14:42
 */
public interface SendEmailCodeService {

    void sendCodeEmail(String to,String text);
}
