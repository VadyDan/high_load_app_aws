package com.example.high_load_app_aws.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AmazonRekognitionService {
    @Autowired
    private AmazonRekognition rekognitionClient;

    public List<DetectEntity> detecting(String bucket, String photo) throws Exception {

        List<DetectEntity> detectEntities = new LinkedList<>();

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image().withS3Object(new S3Object().withName(photo).withBucket(bucket)))
                .withMaxLabels(10).withMinConfidence(75F);

        try {
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            List<Label> labels = result.getLabels();

            System.out.println("Detected labels for " + photo + "\n");
            for (Label label : labels) {
                System.out.println("Label: " + label.getName());
                System.out.println("Confidence: " + label.getConfidence().toString() + "\n");

                List<Instance> instances = label.getInstances();
                System.out.println("Instances of " + label.getName());
                if (instances.isEmpty()) {
                    System.out.println("  " + "None");
                } else {
                    for (Instance instance : instances) {
                        System.out.println("  Confidence: " + instance.getConfidence().toString());
                        System.out.println("  Bounding box: " + instance.getBoundingBox().toString());
                        DetectEntity detectEntity = new DetectEntity();
                        detectEntity.setLabel(label.getName());
                        detectEntity.setConfidence(instance.getConfidence());
                        detectEntity.setBb(List.of(
                                instance.getBoundingBox().getWidth(),
                                instance.getBoundingBox().getHeight(),
                                instance.getBoundingBox().getLeft(),
                                instance.getBoundingBox().getTop()
                        ));
                        detectEntities.add(detectEntity);
                    }
                }
                System.out.println("Parent labels for " + label.getName() + ":");
                List<Parent> parents = label.getParents();
                if (parents.isEmpty()) {
                    System.out.println("  None");
                } else {
                    for (Parent parent : parents) {
                        System.out.println("  " + parent.getName());
                    }
                }
                System.out.println("--------------------");
                System.out.println();

            }
            return detectEntities;
        } catch (AmazonRekognitionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
