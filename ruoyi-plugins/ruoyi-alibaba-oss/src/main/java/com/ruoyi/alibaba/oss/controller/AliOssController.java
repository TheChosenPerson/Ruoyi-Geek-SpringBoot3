package com.ruoyi.alibaba.oss.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.alibaba.oss.domain.AliOssFileVO;
import com.ruoyi.alibaba.oss.utils.AliOssUtil;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/oss")
public class AliOssController {

    @Autowired
    private AliOssUtil aliOssUtil;
    
    @Operation(summary = "下载接口oss")
    @GetMapping("/{client}")
    public void downLoadFile(HttpServletRequest request, HttpServletResponse response,@PathVariable("client") String client,
                            @RequestParam("fileName") String fileName) throws Exception {
        AliOssFileVO file = aliOssUtil.getFile(client, fileName);
        // 设置响应头
        String contentType = file.getHeaders().getOrDefault("Content-Type", "application/octet-stream");
        response.setContentType(contentType);

        // 设置内容长度
        String contentLength = file.getHeaders().get("Content-Length");
        if (contentLength != null) {
            response.setContentLengthLong(Long.parseLong(contentLength));
        }
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        // 写入文件内容到响应流
        try (InputStream inputStream = file.getFileInputSteam();
            OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new IOException("Error writing file to output stream", e);
        }
    }


    //上传接口
    @Operation(summary = "上传接口oss")
    @PutMapping("/{client}")
    public String uploadFile(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("client") String client, @RequestParam("file") MultipartFile file) throws Exception {
        return AliOssUtil.uploadFile(client, file);
    }
}
