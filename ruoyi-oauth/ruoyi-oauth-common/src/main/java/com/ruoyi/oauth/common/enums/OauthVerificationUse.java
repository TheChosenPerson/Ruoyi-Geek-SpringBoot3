package com.ruoyi.oauth.common.enums;

public enum OauthVerificationUse {

    LOGIN("登录", "login"),
    REGISTER("注册", "register"),
    DISABLE("禁用", "disable"),
    RESET_PASSWORD("重置密码", "reset_password"),
    RESET_PHONE("重置手机号", "reset_phone"),
    Other("其他","other");



    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private OauthVerificationUse(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
