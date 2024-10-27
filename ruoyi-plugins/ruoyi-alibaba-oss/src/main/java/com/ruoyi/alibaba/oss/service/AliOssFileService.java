package com.ruoyi.alibaba.oss.service;

import java.io.File;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.alibaba.oss.config.AliOssConfig;
import com.ruoyi.alibaba.oss.domain.AliOssFileVO;
import com.ruoyi.alibaba.oss.utils.AliOssUtil;
import com.ruoyi.common.config.RuoYiConfig;
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
        String relativePath = null;
        if (FileUtils.isAbsolutePath(filePath)) {
            relativePath = FileUtils.getRelativePath(filePath);
        } else {
            relativePath = filePath;
        }

        String fullPath = AliOssUtil.uploadFile(aliOssConfig.getPrimary(), relativePath, file);
        return extractFileName(fullPath);
    }

    @Override
    public String upload(MultipartFile file, String name) throws Exception {
        String fullPath = AliOssUtil.uploadFile(aliOssConfig.getPrimary(), name, file);
        return extractFileName(fullPath);
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
    public InputStream downLoad(String filepath) throws Exception {
        AliOssFileVO file = AliOssUtil.getFile(filepath);
        return file.getFileInputSteam();
    }

    @Override
    public boolean deleteFile(String fileUrl) throws Exception {
        AliOssUtil.removeFile(aliOssConfig.getPrimary(), fileUrl);
        FileOperateUtils.deleteFileAndMd5ByFilePath(fileUrl);
        return true;
    }

    /**
     * 提取文件名
     *
     * @param fullPath 完整的文件路径
     * @return 文件名
     */
    private String extractFileName(String fullPath) {
        if (fullPath == null || !fullPath.contains("fileName=")) {
            return fullPath;
        }
        int startIndex = fullPath.indexOf("fileName=") + "fileName=".length();
        if (startIndex < 0 || startIndex >= fullPath.length()) {
            return fullPath; // 如果没有找到 "fileName="，则返回原路径
        }
        return fullPath.substring(startIndex);
    }
}