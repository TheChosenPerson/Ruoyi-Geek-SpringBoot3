package com.ruoyi.oauth.wx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.auth.common.domain.OauthUser;
import com.ruoyi.auth.common.service.IOauthUserService;
import com.ruoyi.oauth.wx.constant.WxMiniAppConstant;
import com.ruoyi.oauth.wx.constant.WxPubConstant;
import com.ruoyi.oauth.wx.service.Impl.WxLoginServiceImpl;

@RestController
@RequestMapping("/oauth/wx")
public class WxLoginController extends BaseController {
    @Autowired
    public WxPubConstant wxH5AppConstant;

    @Autowired
    public WxMiniAppConstant wxMiniAppConstant;

    @Autowired
    private WxLoginServiceImpl wxLoginServiceImpl;

    @Autowired
    private IOauthUserService oauthUserService;

    @Anonymous
    @PostMapping("/login/{source}/{code}")
    public AjaxResult loginMiniApp(@PathVariable("source") String source, @PathVariable("code") String code) {
        String token = null;
        AjaxResult ajax = AjaxResult.success();
        if ("miniapp".equals(source))
            token = wxLoginServiceImpl.doLoginMiniApp(code);
        else if ("pub".equals(source)) {
            token = wxLoginServiceImpl.doLoginPub(code);
        } else {
            return error("错误的登录方式");
        }
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @PostMapping("/register/{source}/{code}")
    public AjaxResult register(@PathVariable("source") String source, @PathVariable("code") String code) {
        OauthUser oauthUser = oauthUserService.selectOauthUserByUserId(getUserId());
        if (oauthUser != null) {
            return error("不可以重复绑定");
        } else {
            String msg = "";
            oauthUser = new OauthUser();
            oauthUser.setUserId(getUserId());
            oauthUser.setCode(code);
            if ("miniapp".equals(source))
                msg = wxLoginServiceImpl.doRegisterMiniApp(oauthUser);
            else if ("pub".equals(source)) {
                msg = wxLoginServiceImpl.doRegisterPub(oauthUser);
            } else {
                return error("错误的注册方式");
            }
            return StringUtils.isEmpty(msg) ? success() : error(msg);
        }
    }

}
