package com.ruoyi.oauth.common.service;

import com.ruoyi.oauth.common.enums.OauthVerificationUse;

/**
 * code认证方式接口
 * 
 * @author zlh
 * @date 2024-04-16
 */
public interface OauthVerificationCodeService {
    public boolean sendCode(String o, String code,OauthVerificationUse use) throws Exception;
    public String checkCode(String o, String code,OauthVerificationUse use) throws Exception;

}
