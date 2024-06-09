package com.ruoyi.oauth.phone.service;

import com.ruoyi.oauth.common.service.OauthVerificationCodeService;

/**
 * 手机号认证Servcie
 * 
 * @author zlh
 * @date 2024-04-16
 */
public interface DySmsService extends OauthVerificationCodeService {
    public String doLogin(String phone);

}
