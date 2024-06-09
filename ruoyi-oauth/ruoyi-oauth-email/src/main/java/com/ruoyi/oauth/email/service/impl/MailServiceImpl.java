package com.ruoyi.oauth.email.service.impl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.web.service.UserDetailsServiceImpl;
import com.ruoyi.oauth.common.enums.OauthVerificationUse;
import com.ruoyi.oauth.email.service.IMailService;
import com.ruoyi.system.service.ISysUserService;

@Service
public class MailServiceImpl implements IMailService {

    public String CACHE_NAME = "mail_codes";

    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    // application.properties配置的值
    @Value("${spring.mail.username}")
    private String from;

    public boolean beforeSendCode(String email, OauthVerificationUse use) throws Exception {// 1.查验手机号是否存在，分辨登录和删除用户以及注册用户
        boolean haveEmailFlag = userService.selectUserByEmail(email) != null;
        if ((use.equals(OauthVerificationUse.LOGIN) || use.equals(OauthVerificationUse.DISABLE)
                || use.equals(OauthVerificationUse.RESET_PASSWORD)) && !haveEmailFlag) {
            throw new ServiceException("该邮箱未绑定用户");
        } else if ((use.equals(OauthVerificationUse.REGISTER) || use.equals(OauthVerificationUse.RESET_PHONE))
                && haveEmailFlag) {
            throw new ServiceException("该邮箱已绑定用户");
        }
        return true;
    }

    /**
     * 给前端输入的邮箱，发送验证码
     * 
     * @param email
     * @param session
     * @return
     */
    @Override
    public boolean sendMimeMail(String email, String code) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setSubject("验证码邮件"); // 主题
            simpleMailMessage.setText("您收到的验证码是：" + code); // 内容
            simpleMailMessage.setFrom(from); // 发件人
            simpleMailMessage.setTo(email); // 收件人
            mailSender.send(simpleMailMessage); // 发送
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 随机生成6位数的验证码
     * 
     * @return String code
     */
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
    public boolean sendCode(String email, String code, OauthVerificationUse use) throws Exception {
        // 限制短信一分钟只能发送一次短信
        if (CacheUtils.hasKey(CACHE_NAME, email + use.getValue())) {
            throw new ServiceException("请在1分钟后再发送短信");
        }

        try {
            sendMimeMail(email, code);
            CacheUtils.put(CACHE_NAME, email + use.getValue(), code, 1, TimeUnit.MINUTES);
            log.info("发送邮箱验证码成功:{ phone: " + email + " code:" + code + "}");
            return true;
        } catch (Exception e) {
            log.error("发送邮箱验证码异常：" + email);
            throw e;
        }
    }

    @Override
    public String checkCode(String email, String code, OauthVerificationUse use) throws Exception {
        String cachedCode = CacheUtils.get(use.getValue(), use.getValue() + email, String.class); // 从缓存中获取验证码
        CacheUtils.remove(use.getValue(), use.getValue() + email);
        boolean haveEmailFlag = userService.selectUserByEmail(email) != null;
        if (use.equals(OauthVerificationUse.LOGIN) && haveEmailFlag) {// 登录校验
            if (code.equals(cachedCode)) {
                SysUser sysUser = userService.selectUserByEmail(email);
                LoginUser loginUser = (LoginUser) userDetailsServiceImpl.createLoginUser(sysUser);
                return tokenService.createToken(loginUser);
            } else {
                throw new ServiceException("验证码错误");
            }
        } else if (use.equals(OauthVerificationUse.REGISTER) && !haveEmailFlag) {// 注册校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        } else if (use.equals(OauthVerificationUse.DISABLE) && haveEmailFlag) {// 注销校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        } else if (use.equals(OauthVerificationUse.RESET_PASSWORD) && haveEmailFlag) {// 重置密码校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        } else if (use.equals(OauthVerificationUse.RESET_PHONE) && !haveEmailFlag) {// 重置账号校验
            if (code.equals(cachedCode)) {
                return Boolean.toString(true);
            } else {
                throw new ServiceException("验证码错误");
            }
        }
        return Boolean.toString(false);
    }
}
