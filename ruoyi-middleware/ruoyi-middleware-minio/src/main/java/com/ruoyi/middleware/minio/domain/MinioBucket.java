package com.ruoyi.middleware.minio.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.middleware.minio.config.MinioConfig;
import com.ruoyi.middleware.minio.exception.MinioClientErrorException;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import com.ruoyi.common.exception.file.FileException;

public class MinioBucket {

    private MinioClient client;
    private String buketName;

    public MinioBucket() {
    }

    public MinioBucket(MinioClient client, String buketName) {
        this.client = client;
        this.buketName = buketName;
    }

    public String getName() {
        return buketName;
    }

    public MinioClient getClient() {
        return client;
    }

    public String getBuketName() {
        return buketName;
    }

    public void setBuketName(String buketName) {
        this.buketName = buketName;
    }

    public void setClient(MinioClient client) {
        this.client = client;
    }

    public void put(String fileName, MultipartFile file) throws Exception {
        put(fileName, file.getContentType(), file.getInputStream());
    }

    public void put(String filePath, String contentType, InputStream inputStream) throws Exception {
        PutObjectArgs build = PutObjectArgs.builder().contentType(contentType)
                .stream(inputStream, inputStream.available(), -1)
                .bucket(this.buketName).object(filePath).build();
        put(build);
    }

    public void put(PutObjectArgs putObjectArgs) throws Exception {
        try {
            this.client.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new MinioClientErrorException(e.getMessage());
        }
    }

    public void remove(String filePath) throws Exception {
        RemoveObjectArgs build = RemoveObjectArgs.builder().object(filePath).bucket(buketName).build();
        remove(build);
    }

    public void remove(RemoveObjectArgs removeObjectArgs) throws Exception {
        this.client.removeObject(removeObjectArgs);
    }

    public MinioFileVO get(GetObjectArgs getObjectArgs) throws Exception {
        GetObjectResponse inputStream = this.client.getObject(getObjectArgs);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[MinioConfig.maxSize];
        int length = 0;
        while (true) {
            try {
                if (!((length = inputStream.read(bytes, 0, bytes.length)) > 0)) {
                    break;
                }
            } catch (Exception e) {
                throw new FileException("500", new String[] { e.getMessage() });
            }
            byteArrayOutputStream.write(bytes, 0, length);
        }
        return new MinioFileVO(inputStream, inputStream.object(), inputStream.headers(), inputStream.bucket(),
                inputStream.region());
    }

    /**
     * 文件下载
     *
     * @param buketName Minio的桶名
     * @param filePath  文件路径
     * @return 返回封装的Minio下载文件对象
     * @throws IOException 比如读写文件出错时
     */
    public MinioFileVO get(String filePath) throws Exception {
        GetObjectArgs build = GetObjectArgs.builder().object(filePath).bucket(buketName).build();
        return get(build);
    }
}
