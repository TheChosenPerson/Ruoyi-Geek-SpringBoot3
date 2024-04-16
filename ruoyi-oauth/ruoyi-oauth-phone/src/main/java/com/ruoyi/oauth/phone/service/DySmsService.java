package com.ruoyi.oauth.phone.service;

import com.ruoyi.oauth.common.enums.OauthVerificationUse;

/**
 * 手机号认证Servcie
 * 
 * @author zlh
 * @date 2024-04-16
 */
public interface DySmsService {
    public String doLogin(String phone);

    public boolean sendCode(String phone, String code, OauthVerificationUse use) throws Exception;

    public String checkCode(String phone, String code, OauthVerificationUse use) throws Exception;

//    public String doenroll(String phone,String username, String password);


}
