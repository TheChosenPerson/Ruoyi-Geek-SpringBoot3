package com.ruoyi.oauth.justauth.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.oauth.justauth.service.OAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;

/**
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2019/2/19 9:28
 * @since 1.8
 */
@Controller
@RequestMapping("/oauth")
public class OAuthController extends BaseController {

    @Autowired
    private OAuthService oAuthServiceImpl;

    @RequestMapping("/render/{source}")
    @ResponseBody
    public void renderAuth(@PathVariable("source") String source, HttpServletResponse response) throws IOException {
        logger.info("进入render：" + source);
        AuthRequest authRequest = oAuthServiceImpl.getAuthRequest(source);
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        logger.info(authorizeUrl);
        response.sendRedirect(authorizeUrl);
    }

    /**
     * oauth平台中配置的授权回调地址，以本项目为例，在创建github授权应用时的回调地址应为：http://127.0.0.1:8443/oauth/callback/github
     */
    @RequestMapping("/callback/{source}")
    public AjaxResult login(@PathVariable("source") String source, AuthCallback callback,
            HttpServletRequest request) {
        logger.info("进入callback：" + source + " callback params：" + JSONObject.toJSONString(callback));
        AuthRequest authRequest = oAuthServiceImpl.getAuthRequest(source);
        AuthResponse<AuthUser> response = authRequest.login(callback);
        logger.info(JSONObject.toJSONString(response));

        if (response.ok()) {
            oAuthServiceImpl.save(response.getData());
            return success("授权成功！");
        }
        return error(response.getMsg());
    }

    @RequestMapping("/revoke/{source}/{uuid}")
    @ResponseBody
    public AjaxResult revokeAuth(@PathVariable("source") String source, @PathVariable("uuid") String uuid)
            throws IOException {
        AuthRequest authRequest = oAuthServiceImpl.getAuthRequest(source.toLowerCase());

        AuthUser user = oAuthServiceImpl.getByUuid(uuid);
        if (null == user) {
            return error("用户不存在");
        }
        AuthResponse<AuthToken> response = null;
        try {
            response = authRequest.revoke(user.getToken());
            if (response.ok()) {
                oAuthServiceImpl.remove(user.getUuid());
                return success("用户 [" + user.getUsername() + "] 的 授权状态 已收回！");
            }
            return error("用户 [" + user.getUsername() + "] 的 授权状态 收回失败！" + response.getMsg());
        } catch (AuthException e) {
            return error(e.getErrorMsg());
        }
    }

    @RequestMapping("/refresh/{source}/{uuid}")
    @ResponseBody
    public AjaxResult refreshAuth(@PathVariable("source") String source, @PathVariable("uuid") String uuid) {
        AuthRequest authRequest = oAuthServiceImpl.getAuthRequest(source.toLowerCase());

        AuthUser user = oAuthServiceImpl.getByUuid(uuid);
        if (null == user) {
            return error("用户不存在");
        }
        AuthResponse<AuthToken> response = null;
        try {
            response = authRequest.refresh(user.getToken());
            if (response.ok()) {
                user.setToken(response.getData());
                oAuthServiceImpl.save(user);
                return success("用户 [" + user.getUsername() + "] 的 access token 已刷新！新的 accessToken: "
                        + response.getData().getAccessToken());
            }
            return error("用户 [" + user.getUsername() + "] 的 access token 刷新失败！" + response.getMsg());
        } catch (AuthException e) {
            return error(e.getErrorMsg());
        }
    }
}
