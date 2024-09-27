package com.ruoyi.middleware.minio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.middleware.minio.domain.MinioFileVO;
import com.ruoyi.middleware.minio.utils.MinioUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.Headers;

@RestController
@RequestMapping("/minio")
public class MinioController {

    @GetMapping("/{client}")
    @Anonymous
    public void downLoadFile(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("client") String client,
            @RequestParam("fileName") String fileName) throws Exception {
        MinioFileVO file = MinioUtil.getFile(client, fileName);
        Headers headers = file.getHeaders();
        String contentType = headers.get("content-Type");
        response.setContentType(contentType);
        FileUtils.writeBytes(file.getFileInputSteam(), response.getOutputStream());
    }

    @PutMapping("/{client}")
    @Anonymous
    public String uploadFile(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("client") String client, @RequestBody MultipartFile file) throws Exception {
        return MinioUtil.uploadFile(client, file);
    }
}
