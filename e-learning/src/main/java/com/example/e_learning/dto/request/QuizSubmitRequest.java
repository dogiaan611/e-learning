package com.example.e_learning.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmitRequest {

    /**
     * Map từ questionId -> answerId mà học viên đã chọn
     * Ví dụ: { "1": 3, "2": 5, "3": 8 }
     */
    @NotEmpty(message = "Answers cannot be empty")
    private Map<Long, Long> answers;
}
