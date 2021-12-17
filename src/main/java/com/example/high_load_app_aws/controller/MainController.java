package com.example.high_load_app_aws.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.high_load_app_aws.service.AmazonRekognitionService;
import com.example.high_load_app_aws.service.AmazonS3Service;
import com.example.high_load_app_aws.service.DetectEntity;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    AmazonS3Controller amazonS3Controller;

    @Autowired
    private AmazonRekognitionService aRS;

    @GetMapping("/")
    public String welcome(Model model) {
        return "welcome";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, String bucketName, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
        try {
            redirectAttributes.addFlashAttribute("message",
                    amazonS3Controller.fileUpload(bucketName, file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("bucketName") String bucketName, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message",
                amazonS3Controller.softDeleteBucket(bucketName));
        return "redirect:deleteStatus";
    }

    @GetMapping("/deleteStatus")
    public String deleteStatus() {
        return "deleteStatus";
    }

    @GetMapping(value = "/recognize/{bucketName}/{fileName}")
    public String recognize(@PathVariable String bucketName, @PathVariable String fileName, Model model,
                            HttpServletResponse httpResponse) {
        try {
            List<DetectEntity> detectEntities = aRS.detecting(bucketName, fileName);
            model.addAttribute("image", amazonS3.getUrl(bucketName, fileName));
            for (DetectEntity detectEntity:detectEntities) {
                if (detectEntity != null)
                    System.out.println(detectEntity.toString());
            }
            model.addAttribute("bbs", detectEntities);
            return "recognize";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
