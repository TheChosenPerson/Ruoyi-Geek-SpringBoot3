package com.ruoyi.oauth.phone.service.Impl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.web.service.UserDetailsServiceImpl;
import com.ruoyi.oauth.phone.enums.DySmsTemplate;
import com.ruoyi.oauth.phone.service.DySmsService;
import com.ruoyi.oauth.phone.utils.DySmsUtil;
import com.ruoyi.system.service.ISysUserService;

@Service
public class DySmsServiceImpl implements DySmsService {

    @Autowired
    private DySmsUtil dySmsUtil;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private TokenService tokenService;

    public static String generateRandomString(int n) {
        String characters = "0123456789"; //ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }

    @Override
    public String doLogin(String phone) {
        String verify = redisCache.getCacheObject("phone_codes_login" + phone);
        if(verify != null){
            throw new ServiceException("该手机号验证码未过期");
        }
        String code = generateRandomString(6);
        SysUser sysUser = userService.selectUserByPhone(phone);
        if (sysUser == null) {
            throw new ServiceException("该手机号未绑定用户");
        }
        try {
            JSONObject templateParams = new JSONObject();
            templateParams.put("code", code);
            dySmsUtil.sendSms(DySmsTemplate.Test_TEMPLATE_CODE, templateParams, phone);
            redisCache.setCacheObject("phone_codes_login" + phone, code, 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public String doLoginVerify(String phone, String code) {
        String verify = redisCache.getCacheObject("phone_codes_login" + phone);
        if (code.equals(verify)) {
            SysUser sysUser = userService.selectUserByPhone(phone);
            LoginUser loginUser = (LoginUser) userDetailsServiceImpl.createLoginUser(sysUser);
            return tokenService.createToken(loginUser);
        } else {
            throw new ServiceException("验证码错误");
        }
    }

    public String doRegister(String phone) {
        String code = generateRandomString(6);
        redisCache.setCacheObject("phone_codes_register" + phone, code, 1, TimeUnit.MINUTES);
        String verify = redisCache.getCacheObject("phone_codes_register" + phone);
        if(verify != null){
            throw new ServiceException("该手机号验证码未过期");
        }else{
            try {
                dySmsUtil.sendSms(null, null, phone);
                redisCache.setCacheObject("phone_codes_register" + phone, code, 1, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return code;
    }
}
