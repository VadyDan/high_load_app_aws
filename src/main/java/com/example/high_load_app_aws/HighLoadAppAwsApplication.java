package com.example.high_load_app_aws;

import com.example.high_load_app_aws.service.AmazonRekognitionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HighLoadAppAwsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighLoadAppAwsApplication.class, args);
    }

}
