package com.ruoyi.minio.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.minio.utils.FileUploadMinioUtils;

@RestController
@RequestMapping("/minio")
public class MinioController {
    @PostMapping("/upload")
    @Anonymous
    public AjaxResult uploadFileMinio(MultipartFile file) throws Exception {
        try {
            // 上传并返回新文件名称
            String fileName = FileUploadMinioUtils.uploadMinio(file);
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", fileName);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
