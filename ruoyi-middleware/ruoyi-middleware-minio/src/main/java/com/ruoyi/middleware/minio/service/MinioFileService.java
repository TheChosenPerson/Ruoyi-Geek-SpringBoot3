package com.ruoyi.middleware.minio.service;

import java.io.InputStream;

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
import com.ruoyi.middleware.minio.utils.MinioUtil;

/**
 * Minio文件操作实现类
 */
@Component("file:strategy:minio")
@ConditionalOnProperty(prefix = "minio", name = { "enable" }, havingValue = "true", matchIfMissing = false)
public class MinioFileService implements FileService {

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
    };
}
