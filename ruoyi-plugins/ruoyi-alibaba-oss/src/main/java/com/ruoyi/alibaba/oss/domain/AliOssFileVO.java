package com.ruoyi.alibaba.oss.domain;

import com.aliyun.oss.model.ObjectMetadata;
import com.ruoyi.common.core.domain.entity.FileEntity;

public class AliOssFileVO extends FileEntity {
    private String key;
    private String bucketName;
    private ObjectMetadata metadata;

    public AliOssFileVO() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public ObjectMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }
}