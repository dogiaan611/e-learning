package com.example.e_learning.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LessonRequest {

    @NotBlank(message = "Lesson title is required")
    @Size(min = 1, max = 255, message = "Lesson title must be between 1 and 255 characters")
    private String title;

    private String videoUrl;

    private String content;

    @Min(value = 0, message = "Duration must be greater than or equal to 0")
    private Integer duration;

    private Boolean isFree = false;
}

