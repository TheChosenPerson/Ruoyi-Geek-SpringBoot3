package com.ruoyi.middleware.minio.domain;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.text.Convert;
import com.ruoyi.middleware.minio.exception.MinioClientErrorException;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

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

    /**
     * 文件下载
     *
     * @param buketName Minio的桶名
     * @param filePath  文件路径
     * @return 返回封装的Minio下载文件对象
     * @throws IOException 比如读写文件出错时
     */
    public MinioFileVO get(String filePath) throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().object(filePath).bucket(buketName).build();
        GetObjectResponse inputStream = this.client.getObject(getObjectArgs);
        MinioFileVO minioFileVO = new MinioFileVO();

        minioFileVO.setFileInputSteam(inputStream);
        minioFileVO.setByteCount(Convert.toLong(inputStream.headers().get("Content-Length"),null));
        minioFileVO.setFilePath(filePath);
        minioFileVO.setObject(inputStream.object());
        minioFileVO.setRegion(inputStream.region());
        minioFileVO.setBuket(inputStream.bucket());
        minioFileVO.setHeaders(inputStream.headers());
        return minioFileVO;
    }
}
