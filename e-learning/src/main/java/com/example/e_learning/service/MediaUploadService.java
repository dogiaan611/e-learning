package com.example.e_learning.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaUploadService {

    String uploadImage(MultipartFile file);

    String uploadVideo(MultipartFile file);
}

