package com.ruoyi.oauth.email.service;

import com.ruoyi.oauth.common.service.OauthVerificationCodeService;

public interface IMailService extends OauthVerificationCodeService {
    public boolean sendMimeMail(String email, String code);
}
