package com.example.e_learning.dto.response;

import com.example.e_learning.model.enums.EnrollmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
}
