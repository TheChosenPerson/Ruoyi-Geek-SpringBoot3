package com.ruoyi.third.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.third.constant.WxH5Constant;
import com.ruoyi.third.constant.WxMiniAppConstant;
import com.ruoyi.third.service.WxLoginService;
import com.ruoyi.third.utils.HttpClientUtil;

@Service
public class WxLoginServiceImpl implements WxLoginService {
    @Autowired
    private WxMiniAppConstant wxAppConstant;

    @Autowired
    private WxH5Constant wxH5Constant;

    @Autowired
    private HttpClientUtil httpClientUtil;

    public Map<String, String> doLogin(String url, String appid, String secret, String code) {
        String getMessageUrl = url + "?appid=" + appid + "&secret=" + secret + "&js_code=" + code
                + "&grant_type=authorization_code";
        String result = httpClientUtil.sendHttpGet(getMessageUrl);
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

    @Override
    public String doLoginH5(String code) {
        return doLogin(
                wxH5Constant.getUrl(),
                wxH5Constant.getAppId(),
                wxH5Constant.getAppSecret(),
                code).get("openid");
    }

    @Override
    public String doLoginMiniApp(String code) {
        return doLogin(
                wxAppConstant.getUrl(),
                wxAppConstant.getAppId(),
                wxAppConstant.getAppSecret(),
                code).get("openid");
    }

}
