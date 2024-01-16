package com.ruoyi.oauth.wx.service;

public interface WxLoginService {
    public String doLoginMiniApp(String code);

    public String doLoginH5(String code);
}
