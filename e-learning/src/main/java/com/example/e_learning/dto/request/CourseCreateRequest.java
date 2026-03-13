package com.example.e_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseCreateRequest {

    @NotBlank(message = "Course title is required")
    private String title;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;
}

