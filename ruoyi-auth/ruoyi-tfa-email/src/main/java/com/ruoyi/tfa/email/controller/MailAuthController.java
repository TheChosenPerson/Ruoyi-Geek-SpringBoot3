package com.ruoyi.tfa.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.tfa.email.service.impl.MailServiceImpl;

@RestController
@RequestMapping("/auth/mail")
public class MailAuthController extends BaseController {

    @Autowired
    MailServiceImpl serviceImpl;

    @PostMapping("/send/bind") // 发送验证码
    public AjaxResult send(@RequestBody LoginBody loginBody) {
        serviceImpl.doBind(loginBody);
        return success();
    }

    @PostMapping("/verify/bind") // 发送验证码
    public AjaxResult verify(@RequestBody LoginBody loginBody) {
        serviceImpl.doBindVerify(loginBody);
        return success();
    }

    @PostMapping("/send/register")
    @Anonymous
    public AjaxResult sendRegister(@RequestBody RegisterBody registerBody) {
        serviceImpl.doRegister(registerBody);
        return success();
    }

    @PostMapping("/verify/register")
    @Anonymous
    public AjaxResult verifyRegister(@RequestBody RegisterBody registerBody) {
        serviceImpl.doRegisterVerify(registerBody);
        return success();
    }
}
