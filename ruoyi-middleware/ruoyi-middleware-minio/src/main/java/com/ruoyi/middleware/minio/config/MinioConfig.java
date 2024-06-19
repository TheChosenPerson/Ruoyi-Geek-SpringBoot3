package com.ruoyi.middleware.minio.config;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import jakarta.annotation.PostConstruct;
import org.apache.http.impl.io.EmptyInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration("MinioConfiguration")
@ConditionalOnProperty(prefix = "minio", name = { "enable" }, havingValue = "true", matchIfMissing = false)
public class MinioConfig {

    public int maxSize;

    public static String prefix = "/minio";
    @Autowired
    private MinioClientConfig minioClientConfig;

    private Map<String, MinioClientConfig.MinioClientEntity> slaveClients = new ConcurrentHashMap<>();

    private List<MinioClientConfig.MinioClientEntity> slaveClientsList = new CopyOnWriteArrayList<>();

    private MinioClientConfig.MinioClientEntity masterClient;

    @PostConstruct
    public void init() {
        List<MinioClientConfig.MinioClientEntity> collect = minioClientConfig.getSlave().stream().map(item -> {
            setClient(item);
            isBuketExist(item);
            return item;
        }).toList();
        collect.forEach(item -> {
            slaveClients.put(item.getName(), item);
            slaveClientsList.add(item);
        });
        MinioClientConfig.MinioClientEntity master = minioClientConfig.getMaster();
        setClient(master);
        isBuketExist(master);
        masterClient = master;
    }

    public int getMaxSize() {
        return maxSize;
    }

    private void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public List<MinioClientConfig.MinioClientEntity> getSlaveClientsList() {
        return slaveClientsList;
    }

    public Map<String, MinioClientConfig.MinioClientEntity> getSlaveClients() {
        return this.slaveClients;
    }

    public MinioClientConfig.MinioClientEntity getMasterClient() {
        return this.masterClient;
    }

    private static void setClient(MinioClientConfig.MinioClientEntity entity){
        try {
            MinioClient build = MinioClient.builder().endpoint(entity.getUrl())
                    .credentials(entity.getAccessKey(), entity.getSecretKey()).build();
            build.listBuckets();
            entity.setClient(build);
        } catch (Exception exception) {
            MinioClient build = MinioClient.builder().endpoint(entity.getUrl()).build();
            entity.setClient(build);
        }
    }

    private static void isBuketExist(MinioClientConfig.MinioClientEntity entity) {
        try {
            String defaultBuket = entity.getDefaultBuket();
            if(StringUtils.isEmpty(defaultBuket)){
                throw new RuntimeException("defaultBuket without a default value ");
            }
            RemoveObjectArgs remove = RemoveObjectArgs.builder().bucket(entity.getDefaultBuket()).object("test").build();
            entity.getClient().removeObject(remove);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}
