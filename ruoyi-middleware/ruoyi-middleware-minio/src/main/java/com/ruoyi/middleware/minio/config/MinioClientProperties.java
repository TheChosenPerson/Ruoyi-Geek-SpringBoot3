package com.ruoyi.middleware.minio.config;
import io.minio.MinioClient;

public class MinioClientProperties {

    private String url;
    private String accessKey;
    private String secretKey;
    private String buketName;

    private MinioClient client;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBuketName() {
        return buketName;
    }

    public void setBuketName(String buketName) {
        this.buketName = buketName;
    }

    public MinioClient getClient() {
        return client;
    }

    public void setClient(MinioClient client) {
        this.client = client;
    }

    public MinioClientProperties(String url, String accessKey, String secretKey, String buketName) {
        this.url = url;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.buketName = buketName;
    }

    public MinioClientProperties() {
    }
}
