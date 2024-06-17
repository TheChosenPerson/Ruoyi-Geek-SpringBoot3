package com.ruoyi.middleware.minio.utils;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtil;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.middleware.minio.config.MinioConfig;
import com.ruoyi.middleware.minio.domain.MinioFileVO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * Minio文件操作实现类
 */
@Component("file:strategy:minio")
public class MinioFileUtil implements FileUtil {
    private static MinioConfig minioConfig = SpringUtils.getBean(MinioConfig.class);

    @Override
    public String upload(String filePath, MultipartFile file) throws Exception {
        String relativePath = null;
        if (FileUtils.isAbsolutePath(filePath)) {
            relativePath = FileUtils.getRelativePath(filePath);
        } else {
            String absPath = RuoYiConfig.getProfile() + File.separator + filePath;
            relativePath = FileUtils.getRelativePath(absPath);
        }
        return MinioUtil.uploadFile(minioConfig.getMasterClient().getDefaultBuket(), relativePath, file);
    }

    @Override
    public String upload(MultipartFile file, String name) throws Exception {
        return MinioUtil.uploadFile(minioConfig.getMasterClient().getDefaultBuket(), name, file);
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
        String filePath = StringUtils.substringAfter(fileUrl, "?filePath=");
        MinioFileVO file = MinioUtil.getFile(minioConfig.getMasterClient().getDefaultBuket(), filePath);
        return file.getFileInputSteam();
    }

    @Override
    public boolean deleteFile(String fileUrl) throws Exception {
        String filePath = StringUtils.substringAfter(fileUrl, "?filePath=");
        MinioUtil.removeFile(minioConfig.getMasterClient().getDefaultBuket(), filePath);
        return true;
    }
}
