package com.ruoyi.middleware.minio.service;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.domain.entity.FileEntity;
import com.ruoyi.common.service.file.FileService;
import com.ruoyi.common.utils.file.FileOperateUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.middleware.minio.config.MinioConfig;
import com.ruoyi.middleware.minio.domain.MinioFileVO;
import com.ruoyi.middleware.minio.exception.MinioClientErrorException;
import com.ruoyi.middleware.minio.exception.MinioClientNotFundException;
import com.ruoyi.middleware.minio.utils.MinioUtil;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;

/**
 * Minio文件操作实现类
 */
@Component("file:strategy:minio")
@ConditionalOnProperty(prefix = "minio", name = { "enable" }, havingValue = "true", matchIfMissing = false)
public class MinioFileService implements FileService {
    private static final Logger logger = LoggerFactory.getLogger(MinioFileService.class);

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public String upload(String filePath, MultipartFile file) throws Exception {
        return upload(minioConfig.getPrimary(), filePath, file);
    }

    @Override
    public String upload(String baseDir, String filePath, MultipartFile file) throws Exception {
        String relativePath = null;
        if (FileUtils.isAbsolutePath(filePath)) {
            relativePath = FileUtils.getRelativePath(filePath);
        } else {
            relativePath = filePath;
        }
        return MinioUtil.uploadFile(baseDir, relativePath, file);
    }

    @Override
    public InputStream downLoad(String filePath) throws Exception {
        MinioFileVO file = MinioUtil.getFile(minioConfig.getPrimary(), filePath);
        return file.getFileInputSteam();
    }

    @Override
    public boolean deleteFile(String filePath) throws Exception {
        MinioUtil.removeFile(minioConfig.getPrimary(), filePath);
        FileOperateUtils.deleteFileAndMd5ByFilePath(filePath);
        return true;
    }

    @Override
    public FileEntity getFile(String filePath) throws Exception {
        return MinioUtil.getFile(minioConfig.getPrimary(), filePath);
    }

    /**
     * 生成预签名Minio URL.
     *
     * @param filePath 文件路径
     * @return 预签名URL
     * @throws MinioClientNotFundException 如果找不到对应的配置时抛出
     * @throws MinioClientErrorException   如果在创建或获取预签名URL过程中发生错误时抛出
     */
    @Override
    public URL generatePresignedUrl(String filePath) throws MinioClientNotFundException, MinioClientErrorException {
        MinioClient minioClient = null; // 创建并且实例化
        try {
            minioClient = minioConfig.getPrimaryMinioClient(); // 调用封装好的MinioConfig中的方法获取Minio客户端
            String bucketName = minioConfig.getClient().get(minioConfig.getPrimary()).getBucketName();
            GetPresignedObjectUrlArgs request = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(filePath)
                    .expiry(1, TimeUnit.HOURS) // 设置过期时间为1小时
                    .build();
            // 生成预签名URL
            String presignedUrl = minioClient.getPresignedObjectUrl(request);
            URL url = new URL(presignedUrl); // 将字符串形式的预签名URL转换为URL对象并返回
            return url;
        } catch (Exception e) {
            logger.error("生成Minio预签名URL失败: {}", e.getMessage(), e); // 添加日志记录
            throw new MinioClientErrorException("生成Minio预签名URL失败: " + e.getMessage(), e);
        }
    }
}
