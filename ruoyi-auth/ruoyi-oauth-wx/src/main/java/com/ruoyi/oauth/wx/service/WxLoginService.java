package com.ruoyi.oauth.wx.service;

import com.ruoyi.auth.common.domain.OauthUser;

public interface WxLoginService {
    public String doLoginMiniApp(String code);

    public String doLoginPub(String code);


    public String doRegisterPub(OauthUser oauthUser);

    public String doRegisterMiniApp(OauthUser oauthUser);
}
