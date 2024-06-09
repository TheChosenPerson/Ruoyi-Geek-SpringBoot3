package com.ruoyi.pay.wx.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;

/**
 * 配置我们自己的信息
 *
 * @author ZlH
 */
@Configuration
@ConditionalOnProperty(prefix = "pay.wechat", name = "enabled", havingValue = "true")
public class WxPayConfig {

    @Value("${pay.wechat.merchantId}")
    private String wxchantId;
    @Value("${pay.wechat.merchantSerialNumber}")
    private String wxchantSerialNumber;
    @Value("${pay.wechat.apiV3Key}")
    private String wxapiV3Key;
    @Value("${pay.wechat.privateKeyPath}")
    private String wxcertPath;
    @Value("${pay.wechat.appId}")
    private String appId;
    @Value("${pay.wechat.notifyUrl}")
    private String notifyUrl;

    @Bean
    public RSAAutoCertificateConfig wxpayBaseConfig() throws Exception {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(getWxchantId())
                .privateKeyFromPath(getWxcertPath())
                .merchantSerialNumber(getWxchantSerialNumber())
                .apiV3Key(getWxapiV3Key())
                .build();
    }

    @Bean
    public NativePayService nativePayService() throws Exception {
        return new NativePayService.Builder().config(wxpayBaseConfig()).build();
    }

    @Bean
    public NotificationParser notificationParser() throws Exception {
        return new NotificationParser(wxpayBaseConfig());
    }

    @Autowired
    private ApplicationContext applicationContext;

    public String getWxcertPath() throws Exception {
        if (wxcertPath.startsWith("classpath:")) {
            Resource resource = applicationContext.getResource(wxcertPath);
            String tempFilePath = System.getProperty("java.io.tmpdir") + "/temp_wxcert.pem";
            try (InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, Paths.get(tempFilePath), StandardCopyOption.REPLACE_EXISTING);
                wxcertPath = tempFilePath;
            } catch (Exception e) {
                Files.deleteIfExists(Paths.get(tempFilePath));
                throw new RuntimeException("微信支付证书文件读取失败", e);
            }
        }
        return wxcertPath;
    }

    public String getWxchantId() {
        return wxchantId;
    }

    public void setWxchantId(String wxchantId) {
        this.wxchantId = wxchantId;
    }

    public String getWxchantSerialNumber() {
        return wxchantSerialNumber;
    }

    public void setWxchantSerialNumber(String wxchantSerialNumber) {
        this.wxchantSerialNumber = wxchantSerialNumber;
    }

    public String getWxapiV3Key() {
        return wxapiV3Key;
    }

    public void setWxapiV3Key(String wxapiV3Key) {
        this.wxapiV3Key = wxapiV3Key;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
