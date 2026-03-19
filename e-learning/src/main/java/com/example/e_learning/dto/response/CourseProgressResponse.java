package com.example.e_learning.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseProgressResponse {
    private Long courseId;
    private String courseTitle;
    private int totalLessons;
    private int completedLessons;
    private double progressPercent; // 0.0 -> 100.0
    private List<LessonProgressResponse> lessonProgresses;
}
