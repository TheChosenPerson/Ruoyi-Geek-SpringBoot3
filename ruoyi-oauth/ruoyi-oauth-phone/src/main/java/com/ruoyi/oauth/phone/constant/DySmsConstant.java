package com.ruoyi.oauth.phone.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 手机号认证数据
 * 
 * @author Dftre
 * @date 2024-04-16
 */
@Component
public class DySmsConstant {
    @Value("${oauth.phone.dysms.appId}")
    private String accessKeyId;
    @Value("${oauth.phone.dysms.appSecret}")
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
