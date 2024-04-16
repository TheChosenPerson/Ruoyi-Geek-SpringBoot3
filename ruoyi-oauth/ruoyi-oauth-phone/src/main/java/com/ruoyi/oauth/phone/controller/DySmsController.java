package com.ruoyi.oauth.phone.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.oauth.common.enums.OauthVerificationUse;
import com.ruoyi.oauth.phone.service.DySmsService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 手机号认证Controller
 * 
 * @author zlh
 * @date 2024-04-16
 */
@RestController
@RequestMapping("/oauth/phone")
public class DySmsController extends BaseController {
    @Autowired
    public DySmsService dySmsService;

    @Autowired
    private ISysUserService userService;

    @Anonymous
    @PostMapping("/sendcode/{mode}") // 发送验证码
    public AjaxResult sendcode(@RequestBody LoginBody loginBody, @PathVariable(value = "mode") String mode)
            throws Exception {
        try {
            OauthVerificationUse oauthVerificationUse = null;
            switch (mode) {
                case "login":
                    oauthVerificationUse = OauthVerificationUse.LOGIN;
                    break;
                case "register":
                    oauthVerificationUse = OauthVerificationUse.REGISTER;
                    break;
                case "disable":
                    oauthVerificationUse = OauthVerificationUse.DISABLE;
                    break;
                case "resetpassword":
                    oauthVerificationUse = OauthVerificationUse.RESET_PASSWORD;
                    break;
                case "resetphone":
                    oauthVerificationUse = OauthVerificationUse.RESET_PHONE;
                    break;
                default:
                    oauthVerificationUse = OauthVerificationUse.Other;
            }
            String code = generateRandomString(6);
            dySmsService.sendCode(loginBody.getUsername(), code, oauthVerificationUse);
            return AjaxResult.success("发送验证码成功");
        } catch (Exception e) {
            return AjaxResult.error("发送验证码失败，原因: " + e.getMessage());
        }
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

    @Anonymous
    @PostMapping("/login") // 登录
    public AjaxResult postMethodName(@RequestBody LoginBody loginBody) throws Exception {
        if (dySmsService.checkCode(loginBody.getUsername(), loginBody.getCode(),
                OauthVerificationUse.LOGIN) == "false") {
            return AjaxResult.error("登录失败");
        } else {
            return AjaxResult.success("登录成功");
        }
    }

    @Anonymous
    @PostMapping("/register") // 注册
    public AjaxResult postenroll(@RequestBody LoginBody loginBody) throws Exception {
        try {
            dySmsService.checkCode(loginBody.getUsername(), loginBody.getCode(), OauthVerificationUse.REGISTER);
            // 验证通过，执行注册逻辑
            SysUser user = new SysUser();
            user.setUserName(loginBody.getUsername());
            user.setNickName(loginBody.getUsername());
            user.setPassword(SecurityUtils.encryptPassword(loginBody.getPassword()));
            user.setPhonenumber(loginBody.getUsername());
            userService.insertUser(user);
            return AjaxResult.success("注册成功");
        } catch (Exception e) {
            // return "注册失败，原因: " + e.getMessage();
            return AjaxResult.error("注册失败");
        }
    }

    @PostMapping("/disable") // 注销
    public AjaxResult postlogout(@RequestBody LoginBody loginBody) throws Exception {
        try {
            dySmsService.checkCode(getUsername(), loginBody.getCode(), OauthVerificationUse.DISABLE);
            // 验证通过，执行注销逻辑
            SysUser sysUser = userService.selectUserByPhone(loginBody.getUsername());
            userService.deleteUserById(sysUser.getUserId());
            return AjaxResult.success("注销成功");
        } catch (Exception e) {
            return AjaxResult.error("注销失败，原因: " + e.getMessage());
        }
    }
    @Anonymous
    @PostMapping("/resetpassword") // 重置密码
    public AjaxResult postpassword(@RequestBody LoginBody loginBody) throws Exception {
        try {
            dySmsService.checkCode(loginBody.getUsername(), loginBody.getCode(), OauthVerificationUse.RESET_PASSWORD);
            // 验证通过，执行重置密码逻辑
            SysUser sysUser = userService.selectUserByPhone(loginBody.getUsername());
            sysUser.setPassword(SecurityUtils.encryptPassword(loginBody.getPassword()));
            userService.updateUser(sysUser);
            return AjaxResult.success("重置密码成功");
        } catch (Exception e) {
            return AjaxResult.error("重置密码失败，原因: " + e.getMessage());
        }
    }

    @PostMapping("/resetphone") // 重置手机号
    public AjaxResult postphone(@RequestBody LoginBody loginBody) throws Exception {
        try {
            dySmsService.checkCode(loginBody.getUsername(), loginBody.getCode(), OauthVerificationUse.RESET_PHONE);
            // 验证通过，执行重置密码逻辑
            SysUser sysUser = userService.selectUserByUserName(getUsername());
            sysUser.setPhonenumber(loginBody.getUsername());
            userService.updateUser(sysUser);
            return AjaxResult.success("重置手机号成功");
        } catch (Exception e) {
            return AjaxResult.error("重置手机号失败，原因: " + e.getMessage());
        }
    }
}
