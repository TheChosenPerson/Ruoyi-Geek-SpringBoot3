package com.ruoyi.pay.alipay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;

import jakarta.annotation.PostConstruct;

@Component
public class AliPayConfig {
    @Value("${pay.alipay.appId}")
    private String appId;
    @Value("${pay.alipay.privateKey}")
    private String privateKey;
    @Value("${pay.alipay.publicKey}")
    private String publicKey;
    @Value("${pay.alipay.notifyUrl}")
    private String notifyUrl;

    @PostConstruct
    public void init() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi-sandbox.dl.alipaydev.com";
        config.signType = "RSA2";
        config.appId = this.appId;
        config.merchantPrivateKey = this.privateKey;
        config.alipayPublicKey = this.publicKey;
        config.notifyUrl = this.notifyUrl;
        Factory.setOptions(config);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}