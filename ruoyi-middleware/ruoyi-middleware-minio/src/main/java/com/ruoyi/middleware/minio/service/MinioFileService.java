package com.ruoyi.middleware.minio.service;

import java.io.File;
import java.io.InputStream;

import com.ruoyi.common.utils.file.FileOperateUtils;
import com.ruoyi.common.utils.sign.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileService;
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
        String relativePath = null;
        if (FileUtils.isAbsolutePath(filePath)) {
            relativePath = FileUtils.getRelativePath(filePath);
        } else {
            relativePath = filePath;
        }

        return MinioUtil.uploadFile(minioConfig.getPrimary(), relativePath, file);
    }

    @Override
    public String upload(MultipartFile file, String name) throws Exception {
        return MinioUtil.uploadFile(minioConfig.getPrimary(), name, file);
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        String filePath = RuoYiConfig.getProfile() + File.separator + FileUtils.fastFilePath(file);
        return upload(filePath, file);
    }

    @Override
    public String upload(String baseDir, String fileName, MultipartFile file) throws Exception {
        return upload(baseDir + File.pathSeparator + fileName, file);
    }

    @Override
    public InputStream downLoad(String fileUrl) throws Exception {
        String filePath = StringUtils.substringAfter(fileUrl, "?fileName=");
        MinioFileVO file = MinioUtil.getFile(minioConfig.getPrimary(), filePath);
        return file.getFileInputSteam();
    }

    @Override
    public boolean deleteFile(String fileUrl) throws Exception {
        String filePath = StringUtils.substringAfter(fileUrl, "?fileName=");
        MinioUtil.removeFile(minioConfig.getPrimary(), filePath);
        FileOperateUtils.deleteFileAndMd5ByFilePath(filePath);
        return true;
    }
}
