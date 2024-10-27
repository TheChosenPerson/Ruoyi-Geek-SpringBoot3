package com.ruoyi.alibaba.oss.domain;

import java.io.InputStream;
import java.util.Map;

public class AliOssFileVO {
    private InputStream fileInputSteam;
    private String key;
    private Map<String, String> headers;
    private String bucketName;

    public AliOssFileVO(){}
    
    public AliOssFileVO(InputStream fileInputSteam, String key, Map<String, String> headers, String bucketName) {
        this.fileInputSteam = fileInputSteam;
        this.key = key;
        this.headers = headers;
        this.bucketName = bucketName;
    }

    public InputStream getFileInputSteam() {
        return fileInputSteam;
    }

    public void setFileInputSteam(InputStream fileInputSteam) {
        this.fileInputSteam = fileInputSteam;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}