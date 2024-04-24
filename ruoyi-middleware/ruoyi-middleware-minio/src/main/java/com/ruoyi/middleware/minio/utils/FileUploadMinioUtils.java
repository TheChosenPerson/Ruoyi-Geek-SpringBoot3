package com.ruoyi.middleware.minio.utils;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.exception.file.FileNameLengthLimitExceededException;
import com.ruoyi.common.exception.file.FileSizeLimitExceededException;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.middleware.minio.config.MinioConfig;

public class FileUploadMinioUtils extends FileUploadUtils {
     /**
     * Minio默认上传的地址
     */
    private static String bucketName = MinioConfig.getBucketName();

    public static String getBucketName()
    {
        return bucketName;
    }

    /**
     * 以默认BucketName配置上传到Minio服务器
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String uploadMinio(MultipartFile file) throws IOException
    {
        try
        {
            return uploadMinino(getBucketName(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }
    
    /**
     * 自定义bucketName配置上传到Minio服务器
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String uploadMinio(MultipartFile file, String bucketName) throws IOException
    {
        try
        {
            return uploadMinino(bucketName, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    private static final String uploadMinino(String bucketName, MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException
    {
        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH)
        {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }
        assertAllowed(file, allowedExtension);
        try
        {
            String fileName = extractFilename(file);
            String pathFileName = MinioUtil.uploadFile(bucketName, fileName, file);
            return pathFileName;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }
}
