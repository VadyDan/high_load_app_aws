package com.example.high_load_app_aws.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.high_load_app_aws.payload.FileUpload;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AmazonS3Service {
    private static final Logger LOG =  LoggerFactory.getLogger(AmazonS3Service.class);

    @Autowired
    private AmazonS3 amazonS3;

    public String fileUpload(String bucketName, MultipartFile file) {
        String fileName = "";
        try {
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                return "Bucket Not Exist";
            }
            fileName = UUID.randomUUID() + file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType("image/jpeg");
            amazonS3.putObject(
                    new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            LOG.info("File Uploaded");

        } catch (Exception e) {
            LOG.info("File Uploading exception");
            e.printStackTrace();
            return "Exception";
        }
        return "File Uploaded Successfully \nFileName:- " + fileName;
    }

    public String createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(new CreateBucketRequest(bucketName));
            LOG.info("Bucket Created");
            return "Bucket Created \nBucket Name:-" + bucketName +"\nregion:-"
                    + amazonS3.getBucketLocation(new GetBucketLocationRequest(bucketName));
        }
        LOG.info("Bucket Already Exist: " + amazonS3.doesBucketExistV2(bucketName));
        return "Bucket Already Exist";
    }

    public List<String> getBucketList() {
        LOG.info("Getting buckets list!");
        return amazonS3.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
    }

    public List<FileUpload> getBucketFiles(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            LOG.error("No Bucket Found");
            return null;
        }
        return amazonS3.listObjectsV2(bucketName).getObjectSummaries().stream()
                .map(file -> new FileUpload(file.getKey(), file.getSize(), file.getETag()))
                .collect(Collectors.toList());
    }

    public String softDeleteBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            LOG.error("No Bucket Found");
            return "No Bucket Found";
        }
        if (amazonS3.listObjectsV2(bucketName).isTruncated()) {
            amazonS3.deleteBucket(bucketName);
            return "Bucket Deleted Successfully";
        }
        return "Bucket have some files";
    }

    public String hardDeleteBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            LOG.error("No Bucket Found");
            return "No Bucket Found";
        }
        ListObjectsV2Result results = amazonS3.listObjectsV2(bucketName);
        for (S3ObjectSummary s3ObjectSummary : results.getObjectSummaries()) {
            amazonS3.deleteObject(bucketName, s3ObjectSummary.getKey());
            LOG.info(s3ObjectSummary.getKey());
        }
        return "Bucket Deleted Successfully";
    }

    public FileUpload downloadFile(String bucketName, String fileName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            LOG.error("No Bucket Found");
            return null;
        }
        S3Object s3object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        FileUpload fileUpload = new FileUpload();
        try {
            fileUpload.setFile(FileCopyUtils.copyToByteArray(inputStream));
            fileUpload.setFileName(s3object.getKey());
            return fileUpload;
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteFile(String bucketName, String fileName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            LOG.error("No Bucket Found");
            return "No Bucket Found";
        }
        amazonS3.deleteObject(bucketName, fileName);
        return "File Deleted Successfully";
    }
}
