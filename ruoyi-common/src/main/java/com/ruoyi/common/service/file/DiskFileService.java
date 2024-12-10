package com.ruoyi.common.service.file;

import static com.ruoyi.common.utils.file.FileUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.FileEntity;
import com.ruoyi.common.exception.file.FileNameLengthLimitExceededException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileOperateUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.sign.Md5Utils;

/**
 * 磁盘文件操作实现类
 */
@Component("file:strategy:disk")
public class DiskFileService implements FileService {

    @Override
    public String upload(String filePath, MultipartFile file) throws Exception {
        return upload(RuoYiConfig.getProfile(), filePath, file);
    }

    @Override
    public String upload(String baseDir, String filePath, MultipartFile file) throws Exception {
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNamelength > FileUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUtils.DEFAULT_FILE_NAME_LENGTH);
        }
        String absPath = getAbsoluteFile(baseDir + File.separator + filePath).getAbsolutePath();
        file.transferTo(Paths.get(absPath));
        return getPathFileName(filePath);

    }

    @Override
    public InputStream downLoad(String filePath) throws Exception {
        // 本地资源路径
        String localPath = RuoYiConfig.getProfile();
        // 数据库资源地址
        String downloadPath = localPath + StringUtils.substringAfter(filePath, Constants.RESOURCE_PREFIX);
        // 下载名称
        File file = new File(downloadPath);
        if (!file.exists()) {
            throw new FileNotFoundException("未找到文件");
        }
        return new FileInputStream(file);
    }

    @Override
    public boolean deleteFile(String filePath) throws Exception {
        String relivatePath = StringUtils.substringAfter(filePath, Constants.RESOURCE_PREFIX);
        String fileAbs = RuoYiConfig.getProfile() + relivatePath;
        boolean flag = false;
        File file = new File(fileAbs);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            String md5 = Md5Utils.getMd5(file);
            flag = file.delete();
            if (flag) {
                FileOperateUtils.deleteFileAndMd5ByMd5(md5);
            }
        }
        return flag;
    }

    @Override
    public FileEntity getFile(String filePath) throws Exception {
        String localPath = RuoYiConfig.getProfile();
        String downloadPath = localPath + StringUtils.substringAfter(filePath, Constants.RESOURCE_PREFIX);
        File file = new File(downloadPath);
        if (!file.exists()) {
            throw new FileNotFoundException("未找到文件");
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileInputSteam(fileInputStream);
        fileEntity.setByteCount(file.length());
        return fileEntity;
    };
}
