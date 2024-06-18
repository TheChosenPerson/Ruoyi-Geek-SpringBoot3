package com.ruoyi.middleware.minio.config;

import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

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
            try {
                item.setClient(MinioClient.builder().endpoint(item.getUrl())
                        .credentials(item.getAccessKey(), item.getSecretKey()).build());
            } catch (Exception exception) {
                item.setClient(MinioClient.builder().endpoint(item.getUrl()).build());
            }

            return item;
        }).toList();
        collect.forEach(item -> {
            slaveClients.put(item.getName(), item);
            slaveClientsList.add(item);
        });

        MinioClientConfig.MinioClientEntity master = minioClientConfig.getMaster();
        try {
            master.setClient(MinioClient.builder().endpoint(master.getUrl())
                    .credentials(master.getAccessKey(), master.getSecretKey()).build());
        } catch (Exception exception) {
            master.setClient(MinioClient.builder().endpoint(master.getUrl()).build());
        }
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

}
