package com.ruoyi.common.utils.file;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.file.FileNameLengthLimitExceededException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;

import static com.ruoyi.common.utils.file.FileUtils.getAbsoluteFile;
import static com.ruoyi.common.utils.file.FileUtils.getPathFileName;

/**
 * 磁盘文件操作实现类
 */
@Component("file:strategy:disk")
public class DiskFileService implements FileService {

    private static String defaultBaseDir = RuoYiConfig.getProfile();

    public static void setDefaultBaseDir(String defaultBaseDir) {
        DiskFileService.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    @Override
    public String upload(String filePath, MultipartFile file) throws Exception {
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNamelength > FileUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUtils.DEFAULT_FILE_NAME_LENGTH);
        }

        // String fileName = extractFilename(file);

        String absPath = getAbsoluteFile(filePath).getAbsolutePath();
        file.transferTo(Paths.get(absPath));
        return getPathFileName(filePath);
    }

    @Override
    public String upload(MultipartFile file, String name) throws Exception {
        try {
            return upload(getDefaultBaseDir(), file);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        try {
            String filePath = getDefaultBaseDir() + File.separator + DateUtils.dateTime() + File.separator
                    + DateUtils.dateTimeNow() + UUID.fastUUID().toString().substring(0, 6)
                    + "." + FileUtils.getExtension(file);
            return upload(filePath, file);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public String upload(String baseDir, String fileName, MultipartFile file) throws Exception {
        String filePath = RuoYiConfig.getProfile() + File.separator + baseDir + File.separator + fileName;
        return upload(filePath, file);
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
            flag = file.delete();
        }
        return flag;
    }

}
