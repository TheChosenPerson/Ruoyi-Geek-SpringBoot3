package com.ruoyi.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

/**
 * Minio 配置信息
 *
 * @author ruoyi
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * 服务地址
     */
    private static String url;

    /**
     * 用户名
     */
    private static String accessKey;

    /**
     * 密码
     */
    private static String secretKey;

    /**
     * 存储桶名称
     */
    private static String bucketName;

    public static String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        MinioConfig.url = url;
    }

    public static String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        MinioConfig.accessKey = accessKey;
    }

    public static String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        MinioConfig.secretKey = secretKey;
    }

    public static String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        MinioConfig.bucketName = bucketName;
    }

    @Bean
    public MinioClient getMinioClient() {
        try {
            return MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
        } catch (Exception e) {

            return null;
        }

    }
}
