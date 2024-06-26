package com.ruoyi.middleware.minio.exception;

public class MinioClientErrorException extends RuntimeException{
    public MinioClientErrorException(String msg){
        super(msg);
    }
}
