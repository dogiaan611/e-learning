package com.example.e_learning.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.e_learning.service.MediaUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryUploadServiceImpl implements MediaUploadService {

    private final Cloudinary cloudinary;

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;   // 10MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;  // 100MB

    @Override
    public String uploadImage(MultipartFile file) {
        validateFile(file);

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("Image file size must not exceed 10MB. Current size: "
                    + (file.getSize() / (1024 * 1024)) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File must be an image. Received content type: " + contentType);
        }

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "e-learning/images",
                    "transformation", new com.cloudinary.Transformation()
                            .width(1200).height(800).crop("limit")   // resize giới hạn tối đa 1200x800
                            .quality("auto")                          // nén ảnh tự động
                            .fetchFormat("auto")                      // chuyển đổi định dạng tối ưu (webp, avif...)
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(convertToFile(file), params);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public String uploadVideo(MultipartFile file) {
        validateFile(file);

        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new RuntimeException("Video file size must not exceed 100MB. Current size: "
                    + (file.getSize() / (1024 * 1024)) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new RuntimeException("File must be a video. Received content type: " + contentType);
        }

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "video",
                    "folder", "e-learning/videos",
                    "quality", "auto"
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(convertToFile(file), params);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Failed to upload video to Cloudinary", e);
            throw new RuntimeException("Failed to upload video: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File must not be empty");
        }
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload_", "_" + Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        tempFile.deleteOnExit();
        return tempFile;
    }
}

