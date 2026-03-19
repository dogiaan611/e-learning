package com.example.e_learning.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {
    private Long id;
    private String content;
    private Boolean isCorrect; // Chỉ hiển thị cho Instructor/Admin, ẩn với Student khi đang thi
}
