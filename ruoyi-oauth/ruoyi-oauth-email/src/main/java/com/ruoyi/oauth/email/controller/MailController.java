package com.ruoyi.oauth.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.oauth.email.service.IMailService;

@RestController
@Anonymous
@RequestMapping("/mail")
public class MailController extends BaseController {

    @Autowired
    IMailService serviceImpl;

}
