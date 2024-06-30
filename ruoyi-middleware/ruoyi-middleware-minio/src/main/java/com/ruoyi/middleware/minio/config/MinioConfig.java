package com.ruoyi.middleware.minio.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.impl.io.EmptyInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;

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
            isBuketExistOnAnonymous(item);
            return item;
        }).toList();
        collect.forEach(item -> {
            slaveClients.put(item.getName(), item);
            slaveClientsList.add(item);
        });
        MinioClientConfig.MinioClientEntity master = minioClientConfig.getMaster();
        setClient(master);
        isBuketExistOnAnonymous(master);
        masterClient = master;
    }

    public int getMaxSize() {
        return maxSize;
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
        if (StringUtils.isEmpty(entity.getAccessKey())){
            MinioClient build = MinioClient.builder().endpoint(entity.getUrl()).build();
            entity.setClient(build);
        }else {
            MinioClient build = MinioClient.builder().endpoint(entity.getUrl())
                    .credentials(entity.getAccessKey(), entity.getSecretKey()).build();
            entity.setClient(build);
            BucketExistsArgs bucketExistArgs = BucketExistsArgs.builder().bucket(entity.getDefaultBuket()).build();
            try {
                boolean b = entity.getClient().bucketExists(bucketExistArgs);
                if (!b){
                    throw new RuntimeException("defaultBucket does not exist");
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            entity.setClient(build);
        }
    }


    private static void isBuketExistOnAnonymous(MinioClientConfig.MinioClientEntity entity) {
        try {
            String defaultBuket = entity.getDefaultBuket();
            if(StringUtils.isEmpty(defaultBuket)){
                throw new RuntimeException("defaultBuket without a default value ");
            }
            PutObjectArgs putObjectArgs= PutObjectArgs.builder().object(FileUtils.getRelativePath(RuoYiConfig.getProfile())+ "/")
                    .stream(EmptyInputStream.nullInputStream(),0,-1).bucket(entity.getDefaultBuket()).build();
            entity.getClient().putObject(putObjectArgs);

        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}
