package com.example.high_load_app_aws.service;


import java.util.List;

public class DetectEntity {
    private String label;
    private float confidence;
    private List<Float> bb;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public List<Float> getBb() {
        return bb;
    }

    public void setBb(List<Float> bb) {
        this.bb = bb;
    }

    @Override
    public String toString() {
        return "DetectEntity{" +
                "label='" + label + '\'' +
                ", confidence=" + confidence +
                ", bb=" + bb +
                '}';
    }
}
