package com.ruoyi.pay.alipay.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;

/**
 * @author zlh
 */
@Configuration
@ConditionalOnProperty(prefix = "pay.alipay", name = "enabled", havingValue = "true")
public class AliPayConfig {
    @Value("${pay.alipay.appId}")
    private String appId;
    @Value("${pay.alipay.notifyUrl}")
    private String notifyUrl;
    @Value("${pay.alipay.appPrivateKey}")
    private String appPrivateKey;
    @Value("${pay.alipay.alipayPublicKey}")
    private String alipayPublicKey;

    @Autowired
    private ApplicationContext applicationContext;

    private String getAppPrivateKey() throws Exception {
        if (appPrivateKey.startsWith("classpath")) {
            Resource resource = applicationContext.getResource(appPrivateKey);
            InputStream inputStream = resource.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String appPrivateKeyValue = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            bufferedReader.close();
            appPrivateKey = appPrivateKeyValue;
        }
        return appPrivateKey;
    }

    private String getAlipayPublicKey() throws Exception {
        if (alipayPublicKey.startsWith("classpath")) {
            Resource resource = applicationContext.getResource(alipayPublicKey);
            InputStream inputStream = resource.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String alipayPublicKeyValue = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            bufferedReader.close();
            alipayPublicKey = alipayPublicKeyValue;
        }
        return alipayPublicKey;

    }

    @Bean
    protected Config alipayBaseConfig() throws Exception {
        // 设置参数（全局只需设置一次）
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";// openapi-sandbox.dl.alipaydev.com||openapi.alipay.com
        config.signType = "RSA2";
        config.appId = this.appId;
        config.merchantPrivateKey = getAppPrivateKey();
        config.alipayPublicKey = getAlipayPublicKey();
        System.out.println(getAlipayPublicKey());
        config.notifyUrl = this.notifyUrl;
        Factory.setOptions(config);
        return config;
    }

}

// https://openapi-sandbox.dl.alipaydev.com/gateway.do