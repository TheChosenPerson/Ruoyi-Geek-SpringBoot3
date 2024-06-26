package com.ruoyi.common.utils.file;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件上传工具类
 *
 * @author ruoyi
 */
public class FileOperateUtils {

    private static FileService fileService = SpringUtils.getBean("file:strategy:" + RuoYiConfig.getFileServer());
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认上传的地址
     */

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件路径
     * @throws Exception
     */
    public static final String upload(MultipartFile file) throws IOException {
        try {
            FileUtils.assertAllowed(file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
            return fileService.upload(file);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param filePath         上传文件的路径
     * @param file             上传的文件
     * @param allowedExtension 允许的扩展名
     * @return 文件名称
     * @throws IOException
     */
    public static final String upload(String filePath, MultipartFile file, String[] allowedExtension) throws Exception {
        FileUtils.assertAllowed(file, allowedExtension);
        return fileService.upload(filePath, file);
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param fileName         上传文件名
     * @param allowedExtension 允许的扩展名
     * @return 文件名称
     * @throws IOException
     */
    public static final String upload(String baseDir, String fileName, MultipartFile file,
            String[] allowedExtension)
            throws IOException {
        try {
            String filePath = baseDir + File.separator + fileName;
            return upload(filePath, file, allowedExtension);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径下载
     *
     * @param fileUrl      下载文件路径
     * @param outputStream 需要输出到的输出流
     * @return 文件名称
     * @throws IOException
     */
    public static final void downLoad(String fileUrl, OutputStream outputStream) throws Exception {
        InputStream inputStream = fileService.downLoad(fileUrl);
        FileUtils.writeBytes(inputStream, outputStream);
    }

    /**
     * 根据文件路径删除
     *
     * @param fileUrl 下载文件路径
     * @return 是否成功
     * @throws IOException
     */
    public static final boolean deleteFile(String fileUrl) throws Exception {
        return fileService.deleteFile(fileUrl);
    }
}
