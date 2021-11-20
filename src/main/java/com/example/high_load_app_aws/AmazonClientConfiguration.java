package com.example.high_load_app_aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class AmazonClientConfiguration {
    // AmazonS3 Client, in this object – all AWS API calls about S3
    private AmazonS3 amazonS3;

    @Value("${amazon.s3.endpoint}")
    private String url;

    // Your bucket name.
    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    // The IAM access key.
    @Value("${amazon.s3.access-key}")
    private String accessKey;

    // The IAM secret key.
    @Value("${amazon.s3.secret-key}")
    private String secretKey;

    // Getters for parents.
    protected AmazonS3 getClient() {
        return amazonS3;
    }

    protected String getUrl() {
        return url;
    }

    protected String getBucketName() {
        return bucketName;
    }

    @Primary
    @Bean
    public AmazonS3 amazonSQSAsync() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_NORTH_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
