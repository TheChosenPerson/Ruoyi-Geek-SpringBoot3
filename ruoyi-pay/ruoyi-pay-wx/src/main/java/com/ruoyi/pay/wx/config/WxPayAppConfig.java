package com.ruoyi.pay.wx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 配置我们自己的信息
 *
 * @author ZlH
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WxPayAppConfig {

    @Value("${pay.wechat.merchantId}")
     private String wxchantId;
    @Value("${pay.wechat.merchantSerialNumber}")
    private String wxchantSerialNumber;
    @Value("${pay.wechat.apiV3Key}")
    private String wxapiV3Key;
    @Value("${pay.wechat.privateKeyPath}")
    private String wxcertPath;
    @Value("${pay.wechat.appId}")
    private String appId;
    @Value("${pay.wechat.notifyUrl}")
    private String notifyUrl;

}
