package com.ruoyi.oauth.wx.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.auth.common.domain.OauthUser;
import com.ruoyi.auth.common.service.IOauthUserService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpClientUtil;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.web.service.UserDetailsServiceImpl;
import com.ruoyi.oauth.wx.constant.WxMiniAppConstant;
import com.ruoyi.oauth.wx.constant.WxPubConstant;
import com.ruoyi.oauth.wx.service.WxLoginService;
import com.ruoyi.system.service.ISysUserService;

@Service
public class WxLoginServiceImpl implements WxLoginService {
    @Autowired
    private WxMiniAppConstant wxAppConstant;

    @Autowired
    private WxPubConstant wxH5Constant;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private IOauthUserService oauthUserService;

    public Map<String, String> doAuth(String url, String appid, String secret, String code) {
        String getMessageUrl = url + "?appid=" + appid + "&secret=" + secret + "&js_code=" + code
                + "&grant_type=authorization_code";
        String result = HttpClientUtil.sendHttpGet(getMessageUrl);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.containsKey("openid")) {
            String openid = jsonObject.getString("openid");
            String sessionKey = jsonObject.getString("session_key");
            return new HashMap<String, String>() {
                {
                    put("openid", openid);
                    put("sessionKey", sessionKey);
                }
            };
        } else {
            throw new ServiceException(jsonObject.getString("errmsg"), jsonObject.getIntValue("errcode"));
        }
    }

    public String doLogin(String openid) {
        OauthUser selectOauthUser = oauthUserService.selectOauthUserByUUID(openid);
        if (selectOauthUser == null) {
            return null;
        }
        SysUser sysUser = userService.selectUserById(selectOauthUser.getUserId());
        if (sysUser == null) {
            throw new ServiceException("该微信未绑定用户");
        }
        LoginUser loginUser = (LoginUser) userDetailsServiceImpl.createLoginUser(sysUser);
        return tokenService.createToken(loginUser);
    }

    @Override
    public String doLoginPub(String code) {
        String openid = doAuth(
                wxH5Constant.getUrl(),
                wxH5Constant.getAppId(),
                wxH5Constant.getAppSecret(),
                code).get("openid");
        return doLogin(openid);
    }

    @Override
    public String doLoginMiniApp(String code) {
        String openid = doAuth(
                wxAppConstant.getUrl(),
                wxAppConstant.getAppId(),
                wxAppConstant.getAppSecret(),
                code).get("openid");
        return doLogin(openid);
    }

    @Override
    public String doRegisterPub(OauthUser oauthUser) {
        if (StringUtils.isEmpty(oauthUser.getCode())) {
            return "没有凭证";
        }
        if (oauthUser.getUserId() == null) {
            return "请先注册账号";
        }
        Map<String, String> doAuth = doAuth(
                wxH5Constant.getUrl(),
                wxH5Constant.getAppId(),
                wxH5Constant.getAppSecret(),
                oauthUser.getCode());
        oauthUser.setOpenId(doAuth.get("openid"));
        oauthUser.setUuid(doAuth.get("openid"));
        oauthUser.setSource("WXPub");
        oauthUser.setAccessToken(doAuth.get("sessionKey"));
        oauthUserService.insertOauthUser(oauthUser);
        return "";
    }

    @Override
    public String doRegisterMiniApp(OauthUser oauthUser) {
        if (StringUtils.isEmpty(oauthUser.getCode())) {
            return "没有凭证";
        }
        if (oauthUser.getUserId() == null) {
            return "请先注册账号";
        }
        Map<String, String> doAuth = doAuth(
                wxAppConstant.getUrl(),
                wxAppConstant.getAppId(),
                wxAppConstant.getAppSecret(),
                oauthUser.getCode());
        oauthUser.setOpenId(doAuth.get("openid"));
        oauthUser.setUuid(doAuth.get("openid"));
        oauthUser.setSource("WXMiniApp");
        oauthUser.setAccessToken(doAuth.get("sessionKey"));
        oauthUserService.insertOauthUser(oauthUser);
        return "";
    }

}
