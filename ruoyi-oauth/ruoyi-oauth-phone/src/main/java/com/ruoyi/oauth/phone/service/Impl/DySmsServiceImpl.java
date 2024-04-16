package com.ruoyi.oauth.phone.service.Impl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.web.service.UserDetailsServiceImpl;
import com.ruoyi.oauth.common.enums.OauthVerificationUse;
import com.ruoyi.oauth.common.service.OauthVerificationCodeService;
import com.ruoyi.oauth.phone.enums.DySmsTemplate;
import com.ruoyi.oauth.phone.service.DySmsService;
import com.ruoyi.oauth.phone.utils.DySmsUtil;
import com.ruoyi.system.service.ISysUserService;

/**
 * 手机号认证Servcie
 * 
 * @author zlh
 * @date 2024-04-16
 */
@Service
public class DySmsServiceImpl implements DySmsService, OauthVerificationCodeService {

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

    private static final Logger log = LoggerFactory.getLogger(DySmsServiceImpl.class);


   public boolean beforeSendCode(String phone, OauthVerificationUse use) throws Exception {//1.查验手机号是否存在，分辨登录和删除用户以及注册用户
       boolean havePhoneFlag= userService.selectUserByPhone(phone) != null;
       if((use.equals(OauthVerificationUse.LOGIN ) || use.equals(OauthVerificationUse.DISABLE )||use.equals(OauthVerificationUse.RESET_PASSWORD) )&& !havePhoneFlag){
           throw new ServiceException("该手机号未绑定用户");
       }else if((use.equals(OauthVerificationUse.REGISTER ) ||use.equals(OauthVerificationUse.RESET_PHONE))&& havePhoneFlag){
           throw new ServiceException("该手机号已绑定用户");
       }
       return true;
    }

    @Override
    public boolean sendCode(String phone, String code, OauthVerificationUse use) throws Exception {//1.查验手机号是否存在，分辨登录和删除用户以及注册用户
        //限制短信一分钟只能发送一次短信
        String cacheKey = "phone_codes_" + use.getValue() + "_" + phone;
        if(redisCache.hasKey(cacheKey)){
            throw new ServiceException("请在1分钟后再发送短信");
        }

        try {
            JSONObject templateParams = new JSONObject();
            templateParams.put("code", code);
            dySmsUtil.sendSms(DySmsTemplate.Test_TEMPLATE_CODE, templateParams, phone);
            redisCache.setCacheObject("phone_codes_" + use.getValue() + "_" + phone, code, 1, TimeUnit.MINUTES);
            log.info("发送手机验证码成功:{ phone: " + phone + " code:" + code + "}");
            return true;
        } catch (Exception e) {
            log.error("发送手机验证码异常：" + phone);
            throw e;
        }
    }


    @Override
    public String checkCode(String phone, String code, OauthVerificationUse use) throws Exception{
        String cacheKey = "phone_codes_" + use.getValue() + "_" + phone;
        String cachedCode = redisCache.getCacheObject(cacheKey); // 从缓存中获取验证码
        boolean havePhoneFlag= userService.selectUserByPhone(phone) != null;
        if(use.equals(OauthVerificationUse.LOGIN ) && havePhoneFlag){//登录校验
            if (code.equals(cachedCode)) {
                SysUser sysUser = userService.selectUserByPhone(phone);
                LoginUser loginUser = (LoginUser) userDetailsServiceImpl.createLoginUser(sysUser);
                return tokenService.createToken(loginUser);
//                return true;
            } else {
                throw new ServiceException("验证码错误");
            }
        }else if(use.equals(OauthVerificationUse.REGISTER )&& !havePhoneFlag){//注册校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        }else if(use.equals(OauthVerificationUse.DISABLE )&& havePhoneFlag){//注销校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        }else if(use.equals(OauthVerificationUse.RESET_PASSWORD )&& havePhoneFlag){//重置密码校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        }else if(use.equals(OauthVerificationUse.RESET_PHONE )&& !havePhoneFlag){//重置账号校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        }
        return Boolean.toString(false);
    }

    public static String generateRandomString(int n) {
        String characters = "0123456789"; // ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
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
        if (verify != null) {
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
        if (verify != null) {
            throw new ServiceException("该手机号验证码未过期");
        } else {
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
