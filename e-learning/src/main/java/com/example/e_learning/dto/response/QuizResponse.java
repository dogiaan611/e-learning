package com.example.e_learning.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponse {
    private Long id;
    private String title;
    private Long lessonId;
    private String lessonTitle;
    private int totalQuestions;
    private List<QuestionResponse> questions;
}
