package com.ruoyi.middleware.minio.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.exception.file.FileException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.middleware.minio.config.MinioClientConfig;
import com.ruoyi.middleware.minio.config.MinioConfig;
import com.ruoyi.middleware.minio.domain.MinioFileVO;
import com.ruoyi.middleware.minio.exception.MinioClientErrorException;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteError;

/**
 * Minio工具
 * 
 */
public class MinioUtil {

    private static MinioConfig minioConfig;

    private static MinioConfig getMinioConfig() {
        if (minioConfig == null) {
            synchronized (MinioFileService.class) {
                if (minioConfig == null) {
                    minioConfig = SpringUtils.getBean(MinioConfig.class);
                }
            }
        }
        return minioConfig;
    }

    /**
     * 文件上传
     *
     * @param buketName   Minio的桶名
     * @param filePath    上传的文件路径
     * @param contentType 上传文件类型
     * @param inputStream 上传文件的输入流
     * @return 返回上传成功的文路径
     * @throws IOException 比如读写文件出错时
     * 
     */
    public static String uploadFile(String buketName, String filePath,
            String contentType, InputStream inputStream)
            throws Exception {
        PutObjectArgs build = PutObjectArgs.builder().contentType(contentType)
                .stream(inputStream, inputStream.available(), -1)
                .bucket(buketName).object(filePath).build();
        return uploadFile(build);
    }

    /**
     * 文件上传
     *
     * @param putObjectArgs Minio上传参数
     * @return 返回上传成功的文件路径
     */
    public static String uploadFile(PutObjectArgs putObjectArgs) throws Exception {
        try {
            MinioClientConfig.MinioClientEntity masterClient = getMinioConfig().getMasterClient();
            masterClient.getClient().putObject(putObjectArgs);
            StringBuilder url = new StringBuilder();
            url.append(MinioConfig.prefix).append("/").append(masterClient.getDefaultBuket())
                    // .append("/").append(filePath)
                    .append("?").append("fileName=").append(putObjectArgs.object());
            return url.toString();
        } catch (Exception e) {
            throw new MinioClientErrorException(e.getMessage());
        }

    }

    /**
     * 文件上传(从节点递归，直到上传成功)
     *
     * @param index         开始递归的从节点索引
     * @param putObjectArgs Minio上传文件参数
     * @return 返回上传成功的文件路径
     */
    // private static String uploadFileIterator(int index, PutObjectArgs putObjectArgs) {
    //     List<MinioClientConfig.MinioClientEntity> slaveClientsList = getMinioConfig().getSlaveClientsList();
    //     if (index >= slaveClientsList.size()) {
    //         throw new MinioClientNotFundException();
    //     }
    //     try {
    //         MinioClientConfig.MinioClientEntity minioClientEntity = slaveClientsList.get(index);
    //         PutObjectArgs build = PutObjectArgs.builder().contentType(putObjectArgs.contentType())
    //                 .object(putObjectArgs.object())
    //                 .stream(putObjectArgs.stream(), putObjectArgs.stream().available(), -1)
    //                 .bucket(minioClientEntity.getDefaultBuket()).build();
    //         minioClientEntity.getClient().putObject(build);
    //         StringBuilder url = new StringBuilder();
    //         url.append(MinioConfig.prefix).append("/").append(minioClientEntity.getDefaultBuket())
    //                 .append("?").append("fileName=").append(putObjectArgs.object())
    //                 .append("&").append("clientName=").append(minioClientEntity.getName());
    //         return url.toString();
    //     } catch (Exception e) {
    //         return uploadFileIterator(index + 1, putObjectArgs);
    //     }
    // }

    /**
     * 文件上传
     *
     * @param buketName Minio的桶名
     * @param file      上传的文件
     * @param fileName  上传文件名
     * @return 返回上传成功的文件名
     * @throws IOException 比如读写文件出错时
     */
    public static String uploadFile(String buketName, String fileName, MultipartFile file) throws Exception {
        return uploadFile(buketName, fileName, file.getContentType(), file.getInputStream());
    }

    /**
     * 文件上传
     *
     * @param buketName Minio的桶名
     * @param file      上传的文件
     * @return 返回上传成功的文件名
     * @throws IOException 比如读写文件出错时
     */
    public static String uploadFile(String buketName, MultipartFile file) throws Exception {
        String fileName = DateUtils.dateTimeNow() + UUID.fastUUID().toString().substring(0, 5) + "."
                + FileUtils.getExtension(file);

        return uploadFile(buketName, fileName, file.getContentType(), file.getInputStream());
    }

    /**
     * 文件删除
     *
     * @param removeObjectArgs Minio删除文件的参数对象
     * @throws IOException 比如读写文件出错时
     */
    public static void removeFile(RemoveObjectArgs removeObjectArgs) throws Exception {
        getMinioConfig().getMasterClient().getClient().removeObject(removeObjectArgs);
    }

    /**
     * 文件删除
     *
     * @param buketName Minio的桶名
     * @param filePath  上传文件路径
     * @throws IOException 比如读写文件出错时
     */
    public static void removeFile(String buketName, String filePath) throws Exception {
        RemoveObjectArgs build = RemoveObjectArgs.builder().object(filePath).bucket(buketName).build();
        removeFile(build);
    }

    /**
     * 文件批量删除
     *
     * @param removeObjectsArgs Minio批量删除文件参数对象
     * @return 删除结果
     * @throws IOException 比如读写文件出错时
     */
    public static Iterable<Result<DeleteError>> removeFiles(RemoveObjectsArgs removeObjectsArgs) {
        return getMinioConfig().getMasterClient().getClient().removeObjects(removeObjectsArgs);
    }

    /**
     * 文件下载
     *
     * @param getObjectArgs Minio获取文件参数对象
     * @return 返回封装的Minio下载结果对象
     * @throws IOException 比如读写文件出错时
     */
    public static MinioFileVO getFile(GetObjectArgs getObjectArgs) throws Exception {
        GetObjectResponse inputStream = getMinioConfig().getMasterClient().getClient().getObject(getObjectArgs);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[getMinioConfig().maxSize];
        int length = 0;
        while (true) {
            try {
                if (!((length = inputStream.read(bytes, 0, bytes.length)) > 0)) {
                    break;
                }
            } catch (Exception e) {
                throw new FileException("500", new String[] { e.getMessage() });
            }
            byteArrayOutputStream.write(bytes, 0, length);
        }
        return new MinioFileVO(inputStream, inputStream.object(), inputStream.headers(), inputStream.bucket(),
                inputStream.region());
    }

    /**
     * 文件下载
     *
     * @param buketName Minio的桶名
     * @param filePath  文件路径
     * @return 返回封装的Minio下载文件对象
     * @throws IOException 比如读写文件出错时
     */
    public static MinioFileVO getFile(String buketName, String filePath) throws Exception {
        GetObjectArgs build = GetObjectArgs.builder().object(filePath).bucket(buketName).build();
        return getFile(build);
    }

    /**
     * 从节点对应操作工具类
     */
    public static class SlaveClient {
        public static String uploadFile(String clientName, String buketName, String fileName,
                String contentType, InputStream inputStream)
                throws Exception {
            PutObjectArgs build = PutObjectArgs.builder().contentType(contentType)
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(buketName).object(fileName).build();
            return uploadFile(clientName, build);
        }

        public static String uploadFile(String clientName, PutObjectArgs putObjectArgs) throws Exception {
            MinioClientConfig.MinioClientEntity minioClientEntity = getMinioConfig().getSlaveClients().get(clientName);
            minioClientEntity.getClient().putObject(putObjectArgs);
            StringBuilder url = new StringBuilder();
            url.append(MinioConfig.prefix).append("/").append(minioClientEntity.getDefaultBuket())
                    .append("?").append("fileName=").append(putObjectArgs.object())
                    .append("&").append("clientName=").append(minioClientEntity.getName());
            return url.toString();
        }

        public static String uploadFile(String clientName, String buketName, String fileName, MultipartFile file)
                throws Exception {

            return uploadFile(clientName, buketName, fileName, file.getContentType(), file.getInputStream());
        }

        public static String uploadFile(String clientName, String buketName, MultipartFile file) throws Exception {
            String fileName = DateUtils.dateTimeNow() + UUID.fastUUID().toString().substring(0, 5);
            return uploadFile(clientName, buketName, fileName, file.getContentType(), file.getInputStream());
        }

        public static void removeFile(String clientName, RemoveObjectArgs removeObjectArgs) throws Exception {
            getMinioConfig().getSlaveClients().get(clientName).getClient().removeObject(removeObjectArgs);
        }

        public static void removeFile(String clientName, String buketName, String fileName) throws Exception {
            RemoveObjectArgs build = RemoveObjectArgs.builder().object(fileName).bucket(buketName).build();
            removeFile(clientName, build);
        }

        public static Iterable<Result<DeleteError>> removeFiles(RemoveObjectsArgs removeObjectsArgs) {
            return getMinioConfig().getMasterClient().getClient().removeObjects(removeObjectsArgs);
        }

        public static MinioFileVO getFile(String clientName, GetObjectArgs getObjectArgs) throws Exception {
            GetObjectResponse inputStream = getMinioConfig().getSlaveClients().get(clientName).getClient()
                    .getObject(getObjectArgs);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[getMinioConfig().maxSize];
            int length = 0;
            while (true) {
                try {
                    if (!((length = inputStream.read(bytes, 0, bytes.length)) > 0)) {
                        break;
                    }
                } catch (Exception e) {
                    throw new FileException("500", new String[] { e.getMessage() });
                }
                byteArrayOutputStream.write(bytes, 0, length);
            }
            return new MinioFileVO(inputStream, inputStream.object(), inputStream.headers(), inputStream.bucket(),
                    inputStream.region());
        }

        public static MinioFileVO getFile(String clientName, String buketName, String fileName) throws Exception {
            GetObjectArgs build = GetObjectArgs.builder().object(fileName).bucket(buketName).build();
            return getFile(clientName, build);
        }

    }

}
