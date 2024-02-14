package com.ruoyi.oauth.wx.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxPubConstant {
    @Value("${oauth.wx.pub.appId}")
    private String appId;

    @Value("${oauth.wx.pub.appSecret}")
    private String appSecret;

    @Value("${oauth.wx.pub.url}")
    private String url;

    @Value("${oauth.wx.pub.open}")
    private Boolean open;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

}
