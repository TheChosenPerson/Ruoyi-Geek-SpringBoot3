package com.ruoyi.alibaba.oss.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.alibaba.oss.config.AliOssConfig;
import com.ruoyi.alibaba.oss.domain.AliOssFileVO;
import com.ruoyi.alibaba.oss.utils.AliOssUtil;
import com.ruoyi.common.core.domain.entity.FileEntity;
import com.ruoyi.common.service.file.FileService;
import com.ruoyi.common.utils.file.FileOperateUtils;
import com.ruoyi.common.utils.file.FileUtils;

/**
 * oss文件操作实现类
 */
@Component("file:strategy:oss")
@ConditionalOnProperty(prefix = "oss", name = { "enable" }, havingValue = "true", matchIfMissing = false)
public class AliOssFileService implements FileService {
    @Autowired
    private AliOssConfig aliOssConfig;

    @Override
    public String upload(String filePath, MultipartFile file) throws Exception {
        return upload(aliOssConfig.getPrimary(), filePath, file);
    }

    @Override
    public String upload(String baseDir, String filePath, MultipartFile file) throws Exception {
        String relativePath = null;
        if (FileUtils.isAbsolutePath(filePath)) {
            relativePath = FileUtils.getRelativePath(filePath);
        } else {
            relativePath = filePath;
        }
        return AliOssUtil.uploadFile(baseDir, relativePath, file);
    }

    @Override
    public InputStream downLoad(String filepath) throws Exception {
        AliOssFileVO file = AliOssUtil.getFile(filepath);
        return file.getFileInputSteam();
    }

    @Override
    public boolean deleteFile(String filePath) throws Exception {
        AliOssUtil.removeFile(aliOssConfig.getPrimary(), filePath);
        FileOperateUtils.deleteFileAndMd5ByFilePath(filePath);
        return true;
    }

    @Override
    public FileEntity getFile(String filePath) throws Exception {
        return AliOssUtil.getFile(filePath);
    };
}