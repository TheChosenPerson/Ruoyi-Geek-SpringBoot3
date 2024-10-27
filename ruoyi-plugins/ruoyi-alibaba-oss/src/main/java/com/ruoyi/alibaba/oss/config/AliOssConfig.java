package com.ruoyi.alibaba.oss.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.ruoyi.alibaba.oss.domain.AliOssBucket;

@Configuration("AliOssConfiguration")
@ConditionalOnProperty(prefix = "oss", name = "enable", havingValue = "true", matchIfMissing = false)
@ConfigurationProperties(prefix = "oss")
public class AliOssConfig implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(AliOssConfig.class);
    public static int maxSize;
    private String prefix = "/oss"; // 根据需要调整前缀
    private Map<String, AliOssProperties> client = new HashMap<>();
    private String primary;
    private Map<String, AliOssBucket> targetAliOssBucket = new HashMap<>();
    private AliOssBucket masterBucket;

    public int getMaxSize() {
        return maxSize;
    }

    public AliOssBucket getMasterBucket() {
        return this.masterBucket;
    }

    public AliOssBucket getBucket(String client) {
        return targetAliOssBucket.get(client);
    }

    public Map<String, AliOssProperties> getClient() {
        return client;
    }

    public void setClient(Map<String, AliOssProperties> client) {
        this.client = client;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (client == null || client.isEmpty()) {
            throw new RuntimeException("Client properties cannot be null or empty");
        }

        client.forEach((name, props) -> {
            try {
                AliOssBucket aliOssBucket = createOssClient(name, props);
                targetAliOssBucket.put(name, aliOssBucket);
            } catch (Exception e) {
                logger.error("Failed to create OSS client for {}: {}", name, e.getMessage(), e);
            }
        });

        if (targetAliOssBucket.get(primary) == null) {
            throw new RuntimeException("Primary client " + primary + " does not exist");
        }
        masterBucket = targetAliOssBucket.get(primary);
    }

    private AliOssBucket createOssClient(String name, AliOssProperties props) {
        if (props == null || props.getEndpoint() == null || props.getAccessKeyId() == null ||
            props.getAccessKeySecret() == null || props.getBucketName() == null) {
            throw new IllegalArgumentException("AliOssProperties or its required fields cannot be null");
        }
        
        OSS client = new OSSClientBuilder().build(props.getEndpoint(), props.getAccessKeyId(), props.getAccessKeySecret());
        AliOssBucket ossBucket = new AliOssBucket(client, props.getBucketName());
        validateOssBucket(ossBucket);
        logger.info("数据桶：{} - 链接成功", name);
        return ossBucket;
    }

    private static void validateOssBucket(AliOssBucket aliOssBucket) {
        OSS ossClient = aliOssBucket.getOssClient();
        String bucketName = aliOssBucket.getBucketName();
        try {
            if (!ossClient.doesBucketExist(bucketName)) {
                throw new RuntimeException("Bucket " + bucketName + " does not exist");
            }
        } catch (OSSException oe) {
            logger.error("OSSException: " + oe.getMessage(), oe);
            throw new RuntimeException("OSS error: " + oe.getMessage());
        } catch (ClientException ce) {
            logger.error("ClientException: " + ce.getMessage(), ce);
            throw new RuntimeException("Client error: " + ce.getMessage());
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage(), e);
            throw new RuntimeException("Error validating OSS bucket: " + e.getMessage());
        }
    }

    
    public String getMasterBucketName() {
        if (masterBucket != null) {
            return masterBucket.getBucketName();
        }
        return null; // 或者抛出异常，根据业务需求决定
    }

    public String getBucketName(String client) {
        if (client != null && targetAliOssBucket.containsKey(client)) {
            return targetAliOssBucket.get(client).getBucketName();
        }
        return null; // 或者抛出异常，根据业务需求决定
    }

}
