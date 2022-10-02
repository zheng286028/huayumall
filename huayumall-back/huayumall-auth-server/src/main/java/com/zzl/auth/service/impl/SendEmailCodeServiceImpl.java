package com.zzl.auth.service.impl;

import com.zzl.auth.service.SendEmailCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/29  14:44
 */
@Service
public class SendEmailCodeServiceImpl implements SendEmailCodeService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送验证码
     */
    @Override
    public void sendCodeEmail(String to,String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("华宇商城");
        message.setText(text);
        javaMailSender.send(message);
    }
}
