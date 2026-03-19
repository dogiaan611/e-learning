package com.example.e_learning.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResultResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long quizId;
    private String quizTitle;
    private Double score;         // Điểm phần trăm (0.0 - 100.0)
    private int totalQuestions;
    private int correctAnswers;
    private LocalDateTime completedAt;
    private boolean passed;       // Qua bài hay chưa (>= 50%)
}
