package com.ruoyi.alibaba.oss.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.alibaba.oss.domain.AliOssFileVO;
import com.ruoyi.alibaba.oss.utils.AliOssUtil;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.utils.file.FileUtils;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/oss")
public class AliOssController {

    @Operation(summary = "下载接口oss")
    @GetMapping("/{client}")
    @Anonymous
    public void downLoadFile(HttpServletResponse response,
            @PathVariable("client") String client,
            @RequestParam("fileName") String fileName) throws Exception {
        AliOssFileVO file = AliOssUtil.getFile(client, fileName);
        FileUtils.setAttachmentResponseHeader(response, FileUtils.getName(fileName));
        response.setContentLengthLong(file.getByteCount());
        FileUtils.writeBytes(file.getFileInputSteam(), response.getOutputStream());
    }

    // 上传接口
    @Operation(summary = "上传接口oss")
    @PutMapping("/{client}")
    public String uploadFile(@PathVariable("client") String client, @RequestParam("file") MultipartFile file)
            throws Exception {
        return AliOssUtil.uploadFile(client, file);
    }
}
