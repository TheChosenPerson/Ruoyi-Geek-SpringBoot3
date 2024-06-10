package com.ruoyi.tfa.phone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 手机号认证数据
 * 
 * @author Dftre
 * @date 2024-04-16
 */
@Configuration
public class DySmsConfig {
    @Value("${tfa.phone.dysms.appId}")
    private String accessKeyId;
    @Value("${tfa.phone.dysms.appSecret}")
    private String accessKeySecret;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

}
