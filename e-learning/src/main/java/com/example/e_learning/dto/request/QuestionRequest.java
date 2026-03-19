package com.example.e_learning.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {

    @NotBlank(message = "Question content is required")
    private String content;

    @NotEmpty(message = "At least one answer is required")
    @Size(min = 2, message = "Question must have at least 2 answers")
    @Valid
    private List<AnswerRequest> answers;
}
