package com.ruoyi.oauth.justauth.service;

import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;

public interface OAuthService {
    /**
     * 根据具体的授权来源，获取授权请求工具类
     *
     * @param source
     * @return
     */
    public AuthRequest getAuthRequest(String source);

    AuthUser save(AuthUser user);

    AuthUser getByUuid(String uuid);

    void remove(String uuid);

}
