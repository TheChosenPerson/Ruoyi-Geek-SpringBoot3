package com.ruoyi.middleware.minio.domain;

import okhttp3.Headers;

import java.io.InputStream;

public class MinioFileVO {
    private InputStream fileInputSteam;
    private String object;
    private Headers headers;
    private String buket;
    private String region;

    public MinioFileVO() {
    }

    public InputStream getFileInputSteam() {
        return fileInputSteam;
    }

    public void setFileInputSteam(InputStream fileInputSteam) {
        this.fileInputSteam = fileInputSteam;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public String getBuket() {
        return buket;
    }

    public void setBuket(String buket) {
        this.buket = buket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public MinioFileVO(InputStream fileInputSteam, String object, Headers headers, String buket, String region) {
        this.fileInputSteam = fileInputSteam;
        this.object = object;
        this.headers = headers;
        this.buket = buket;
        this.region = region;
    }

}
