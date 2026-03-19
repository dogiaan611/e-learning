package com.example.e_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerRequest {

    @NotBlank(message = "Answer content is required")
    private String content;

    @NotNull(message = "isCorrect flag is required")
    private Boolean isCorrect;
}
