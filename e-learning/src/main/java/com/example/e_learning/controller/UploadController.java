package com.example.e_learning.controller;

import com.example.e_learning.dto.response.UploadResponse;
import com.example.e_learning.service.MediaUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final MediaUploadService mediaUploadService;

    @PostMapping("/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = mediaUploadService.uploadImage(file);
        return ResponseEntity.ok(UploadResponse.builder().url(url).build());
    }

    @PostMapping("/video")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<UploadResponse> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = mediaUploadService.uploadVideo(file);
        return ResponseEntity.ok(UploadResponse.builder().url(url).build());
    }
}

