package com.ruoyi.common.service.file;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.domain.entity.FileEntity;

//默认上传下载
/**
 * 文件操作接口
 */
public interface FileService {

    /**
     * 文件上传
     *
     * @param filePath 上传的文件路径
     * @param file     文件对象
     * @return 返回上传成功的文路径
     * @throws IOException 比如读写文件出错时
     *
     */
    public String upload(String filePath, MultipartFile file) throws Exception;

    /**
     * 文件上传
     *
     * @param filePath 上传的文件路径
     * @param file     文件对象
     * @return 返回上传成功的文路径
     * @throws IOException 比如读写文件出错时
     *
     */
    public String upload(MultipartFile file, String name) throws Exception;

    /**
     * 文件上传
     *
     * @param file 文件对象
     * @return 返回上传成功的文路径
     * @throws IOException 比如读写文件出错时
     *
     */
    public String upload(MultipartFile file) throws Exception;

    /**
     * 文件上传
     *
     * @param baseDir  上传的文件基路径
     * @param fileName 文件名称
     * @param file     文件对象
     * @return 返回上传成功的文路径
     * @throws IOException 比如读写文件出错时
     *
     */
    public String upload(String baseDir, String fileName, MultipartFile file) throws Exception;

    /**
     * 文件下载
     *
     * @param filePath 下载的文件路径
     * @return 返回上传成功的文路径
     * @throws IOException 比如读写文件出错时
     *
     */
    public InputStream downLoad(String filePath) throws Exception;

    /**
     * 文件下载
     *
     * @param filePath 删除的文件路径
     * @return 返回上传成功的文路径
     * @throws IOException 比如读写文件出错时
     *
     */
    public boolean deleteFile(String filePath) throws Exception;

    /**
     * 获取文件
     * 
     * @param filePath
     * @return
     * @throws Exception
     */
    public FileEntity getFile(String filePath) throws Exception;
}
