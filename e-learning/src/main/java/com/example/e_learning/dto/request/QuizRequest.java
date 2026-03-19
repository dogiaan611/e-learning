package com.example.e_learning.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    private Long lessonId;
}
