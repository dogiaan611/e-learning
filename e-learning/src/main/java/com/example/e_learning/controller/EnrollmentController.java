package com.example.e_learning.controller;

import com.example.e_learning.dto.response.EnrollmentResponse;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;

    /**
     * Học viên đăng ký một khóa học
     * POST /api/v1/enrollments/courses/{courseId}
     */
    @PostMapping("/courses/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnrollmentResponse> enrollCourse(@PathVariable Long courseId,
                                                           Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        EnrollmentResponse response = enrollmentService.enrollCourse(user.getId(), courseId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Học viên xem danh sách khóa học đã đăng ký của mình
     * GET /api/v1/enrollments/my
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(user.getId()));
    }

    /**
     * Kiểm tra xem học viên đã đăng ký khóa học này chưa
     * GET /api/v1/enrollments/courses/{courseId}/status
     */
    @GetMapping("/courses/{courseId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnrollmentResponse> getEnrollmentStatus(@PathVariable Long courseId,
                                                                   Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(enrollmentService.getEnrollmentStatus(user.getId(), courseId));
    }

    /**
     * Học viên tự thoát khóa học
     * DELETE /api/v1/enrollments/courses/{courseId}
     */
    @DeleteMapping("/courses/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unenrollCourse(@PathVariable Long courseId,
                                               Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        enrollmentService.unenrollCourse(user.getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin/Teacher xóa một enrollment bất kỳ theo ID
     * DELETE /api/v1/enrollments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> removeEnrollment(@PathVariable Long id) {
        enrollmentService.removeEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
