package com.example.e_learning.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonProgressResponse {
    private Long lessonId;
    private String lessonTitle;
    private Boolean isCompleted;
    private Integer lastWatchedTime; // Đang xem đến giây thứ bao nhiêu
}
