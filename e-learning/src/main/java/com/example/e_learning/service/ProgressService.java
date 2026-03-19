package com.example.e_learning.service;

import com.example.e_learning.dto.request.ProgressUpdateRequest;
import com.example.e_learning.dto.response.CourseProgressResponse;
import com.example.e_learning.dto.response.LessonProgressResponse;

public interface ProgressService {

    // Cập nhật tiến độ bài học (mark complete hoặc lưu thời gian đang xem)
    LessonProgressResponse updateLessonProgress(Long userId, Long lessonId, ProgressUpdateRequest request);

    // Lấy tiến độ tổng thể của user trong một khóa học (% + chi tiết từng bài)
    CourseProgressResponse getCourseProgress(Long userId, Long courseId);
}
