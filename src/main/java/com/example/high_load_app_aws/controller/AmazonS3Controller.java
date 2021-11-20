package com.example.high_load_app_aws.controller;

import com.example.high_load_app_aws.payload.FileUpload;
import com.example.high_load_app_aws.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class AmazonS3Controller {

    @Autowired
    private AmazonS3Service amazonS3Service;


    @PostMapping(value = "/bucket/create/{bucketName}")
    public String createBucket(@PathVariable String bucketName) {
        return amazonS3Service.createBucket(bucketName);
    }

    @GetMapping(value = "/bucket/list")
    public List<String> getBucketList() {
        return amazonS3Service.getBucketList();
    }

    @GetMapping(value = "/bucket/files/{bucketName}")
    public List<FileUpload> getBucketFiles(@PathVariable String bucketName) {
        return amazonS3Service.getBucketFiles(bucketName);
    }

    @DeleteMapping(value = "/bucket/delete/hard/{bucketName}")
    public String hardDeleteBucket(@PathVariable String bucketName) {
        return amazonS3Service.hardDeleteBucket(bucketName);
    }

    @DeleteMapping(value = "/bucket/delete/{bucketName}")
    public String softDeleteBucket(@PathVariable String bucketName) {
        return amazonS3Service.softDeleteBucket(bucketName);
    }

    @PostMapping(value = "/file/upload/{bucketName}")
    public String fileUpload(@PathVariable String bucketName, MultipartFile file) {
        return amazonS3Service.fileUpload(bucketName, file);
    }

    @PostMapping(value = "/file/delete/{bucketName}/{fileName}")
    public String deleteFile(@PathVariable String bucketName, @PathVariable String fileName) {
        return amazonS3Service.deleteFile(bucketName, fileName);
    }

    @GetMapping(value = "/file/download/{bucketName}/{fileName}")
    public StreamingResponseBody downloadFile(@PathVariable String bucketName, @PathVariable String fileName,
                                              HttpServletResponse httpResponse) {
        FileUpload downloadFile = amazonS3Service.downloadFile(bucketName, fileName);
        httpResponse.setContentType("application/octet-stream");
        httpResponse.setHeader("Content-Disposition",
                String.format("inline; filename=\"%s\"", downloadFile.getFileName()));
        return outputStream -> {
            outputStream.write(downloadFile.getFile());
            outputStream.flush();
        };
    }
}
