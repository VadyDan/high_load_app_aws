package com.example.high_load_app_aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class AmazonClientConfiguration {
    private AmazonS3 amazonS3;
    private AmazonRekognition amazonRekognition;

    @Value("${amazon.s3.endpoint}")
    private String url;

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    @Value("${amazon.s3.access-key}")
    private String accessKey;

    @Value("${amazon.s3.secret-key}")
    private String secretKey;

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
                .withRegion(Regions.EU_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Primary
    @Bean
    public AmazonRekognition amazonRAsync() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return this.amazonRekognition = AmazonRekognitionClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
