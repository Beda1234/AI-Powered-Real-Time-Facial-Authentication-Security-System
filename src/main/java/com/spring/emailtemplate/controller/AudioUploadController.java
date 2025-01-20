package com.spring.emailtemplate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.emailtemplate.emailservice.GoogleApiAudioUploadService;

@RestController
@RequestMapping("/api/audio")
public class AudioUploadController {

    @Autowired
    private GoogleApiAudioUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("displayName") String displayName) {
        try {
            String response = uploadService.processAudioUpload(file, displayName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
