package com.ruoyi.tfa.email.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.tfa.email.config.EmailConfig;

public class EmailUtil {
    public static void sendMessage(String email, String title, String message) {
        EmailConfig emailConfig = SpringUtils.getBean(EmailConfig.class);
        JavaMailSenderImpl mailSender = SpringUtils.getBean(JavaMailSenderImpl.class);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(emailConfig.getFrom());
        simpleMailMessage.setTo(email);
        mailSender.send(simpleMailMessage);
    }
}
