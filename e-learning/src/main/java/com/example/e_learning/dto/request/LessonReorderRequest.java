package com.example.e_learning.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class LessonReorderRequest {

    @NotEmpty(message = "Lesson IDs list must not be empty")
    private List<Long> lessonIds;
}

