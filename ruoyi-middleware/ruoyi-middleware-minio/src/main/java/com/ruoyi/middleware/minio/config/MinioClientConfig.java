package com.ruoyi.middleware.minio.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("minio.client")
public class MinioClientConfig {

    public MinioClientEntity getMaster() {
        return master;
    }

    public void setMaster(MinioClientEntity master) {
        this.master = master;
    }

    public List<MinioClientEntity> getSlave() {
        return slave;
    }

    public void setSlave(List<MinioClientEntity> slave) {
        this.slave = slave;
    }

    private MinioClientEntity master;

    private List<MinioClientEntity> slave = new ArrayList<>();

    public static class MinioClientEntity {

        private String url;
        private String accessKey;
        private String secretKey;
        private String name;
        private String defaultBuket;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDefaultBuket() {
            return defaultBuket;
        }

        public void setDefaultBuket(String defaultBuket) {
            this.defaultBuket = defaultBuket;
        }

        public MinioClient getClient() {
            return client;
        }

        public void setClient(MinioClient client) {
            this.client = client;
        }

        public MinioClientEntity(String url, String accessKey, String secretKey, String name, String defaultBuket) {
            this.url = url;
            this.accessKey = accessKey;
            this.secretKey = secretKey;
            this.name = name;
            this.defaultBuket=defaultBuket;
        }

        public MinioClientEntity() {
        }
    }
}
