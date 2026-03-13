package com.example.e_learning.dto.request;

import com.example.e_learning.model.enums.CourseLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseUpdateRequest {

    @Size(min = 1, message = "Title must not be empty")
    private String title;

    private String description;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;

    private String thumbnailUrl;

    private CourseLevel level;
}

