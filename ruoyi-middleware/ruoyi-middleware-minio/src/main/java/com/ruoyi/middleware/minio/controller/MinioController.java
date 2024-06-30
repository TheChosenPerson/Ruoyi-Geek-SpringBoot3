package com.ruoyi.middleware.minio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/{buketName}")
    @Anonymous
    public void downLoadFile(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("buketName") String buketName,
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "clientName", required = false) String clientName) throws Exception {

        if (!StringUtils.isEmpty(clientName)) {
            MinioFileVO file = MinioUtil.SlaveClient.getFile(clientName, buketName, fileName);
            Headers headers = file.getHeaders();

            String contentType = headers.get("content-Type");
            response.setContentType(contentType);
            FileUtils.writeBytes(file.getFileInputSteam(), response.getOutputStream());
        } else {
            MinioFileVO file = MinioUtil.getFile(buketName, fileName);

            Headers headers = file.getHeaders();
            String contentType = headers.get("content-Type");
            response.setContentType(contentType);
            FileUtils.writeBytes(file.getFileInputSteam(), response.getOutputStream());
        }
    }
}
