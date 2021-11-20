package com.example.high_load_app_aws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
    @Autowired
    AmazonS3Controller amazonS3Controller;

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
}
