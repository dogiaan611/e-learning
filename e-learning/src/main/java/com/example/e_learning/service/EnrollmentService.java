package com.example.e_learning.service;

import com.example.e_learning.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    // Học viên đăng ký khóa học
    EnrollmentResponse enrollCourse(Long userId, Long courseId);

    // Học viên xem danh sách khóa học đã đăng ký
    List<EnrollmentResponse> getMyEnrollments(Long userId);

    // Kiểm tra trạng thái đăng ký của học viên với một khóa học
    EnrollmentResponse getEnrollmentStatus(Long userId, Long courseId);

    // Học viên tự thoát khóa học
    void unenrollCourse(Long userId, Long courseId);

    // Admin/Teacher xóa 1 enrollment bất kỳ theo enrollmentId
    void removeEnrollment(Long enrollmentId);
}
