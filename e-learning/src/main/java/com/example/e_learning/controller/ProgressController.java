package com.example.e_learning.controller;

import com.example.e_learning.dto.request.ProgressUpdateRequest;
import com.example.e_learning.dto.response.CourseProgressResponse;
import com.example.e_learning.dto.response.LessonProgressResponse;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final UserRepository userRepository;

    /**
     * Cập nhật tiến độ bài học: đánh dấu hoàn thành hoặc lưu thời gian đang xem
     * PUT /api/v1/progress/lessons/{lessonId}
     *
     * Body: { "isCompleted": true, "lastWatchedTime": 120 }
     */
    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LessonProgressResponse> updateLessonProgress(
            @PathVariable Long lessonId,
            @RequestBody ProgressUpdateRequest request,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        LessonProgressResponse response = progressService.updateLessonProgress(user.getId(), lessonId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy tiến độ tổng thể của user trong 1 khóa học (% + chi tiết từng bài)
     * GET /api/v1/progress/courses/{courseId}
     */
    @GetMapping("/courses/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            @PathVariable Long courseId,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        CourseProgressResponse response = progressService.getCourseProgress(user.getId(), courseId);
        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
