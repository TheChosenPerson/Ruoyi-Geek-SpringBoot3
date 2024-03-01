package com.ruoyi.oauth.phone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.oauth.phone.service.DySmsService;


@RestController
@Anonymous
@RequestMapping("/oauth/phone")
public class DySmsController {
    @Autowired
    public DySmsService dySmsService;
    @PostMapping("/login")
    public String postMethodName() {
        dySmsService.doLogin("17854126030");
        return null;
    }
    
}
