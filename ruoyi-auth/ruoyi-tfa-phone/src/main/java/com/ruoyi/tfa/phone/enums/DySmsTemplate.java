package com.ruoyi.tfa.phone.enums;

import com.ruoyi.common.utils.StringUtils;

/**
 * 手机号认证短信模板
 * 
 * @author Dftre
 * @date 2024-04-16
 */
public enum DySmsTemplate {
    /** 登录短信模板编码 */
    LOGIN_TEMPLATE_CODE("SMS_175435174", "Ruoyi", "code"),
    /** 忘记密码短信模板编码 */
    FORGET_PASSWORD_TEMPLATE_CODE("SMS_175435174", "Ruoyi", "code"),
    /** 测试 */
    Test_TEMPLATE_CODE("SMS_154950909", "阿里云短信测试", "code");

    /**
     * 短信模板编码
     */
    private String templateCode;
    /**
     * 签名
     */
    private String signName;
    /**
     * 短信模板必需的数据名称，多个key以逗号分隔，此处配置作为校验
     */
    private String keys;

    private DySmsTemplate(String templateCode, String signName, String keys) {
        this.templateCode = templateCode;
        this.signName = signName;
        this.keys = keys;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public static DySmsTemplate toEnum(String templateCode) {
        if (StringUtils.isEmpty(templateCode)) {
            return null;
        }
        for (DySmsTemplate item : DySmsTemplate.values()) {
            if (item.getTemplateCode().equals(templateCode)) {
                return item;
            }
        }
        return null;
    }
}
