package com.example.e_learning.service.impl;

import com.example.e_learning.dto.response.EnrollmentResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Course;
import com.example.e_learning.model.Enrollment;
import com.example.e_learning.model.User;
import com.example.e_learning.model.enums.CourseStatus;
import com.example.e_learning.model.enums.EnrollmentStatus;
import com.example.e_learning.repository.CourseRepository;
import com.example.e_learning.repository.EnrollmentRepository;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EnrollmentResponse enrollCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Chỉ cho phép đăng ký khóa học đã PUBLISHED
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Cannot enroll in a course that is not published. Current status: " + course.getStatus());
        }

        // Kiểm tra đã đăng ký chưa để tránh trùng lặp
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("User is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.COMPLETED) // COMPLETED = đăng ký thành công
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToResponse(saved);
    }

    @Override
    public List<EnrollmentResponse> getMyEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponse getEnrollmentStatus(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "userId/courseId",
                        userId + "/" + courseId));
        return mapToResponse(enrollment);
    }

    @Override
    @Transactional
    public void unenrollCourse(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "userId/courseId",
                        userId + "/" + courseId));
        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional
    public void removeEnrollment(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new ResourceNotFoundException("Enrollment", "id", enrollmentId);
        }
        enrollmentRepository.deleteById(enrollmentId);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUser().getId())
                .userFullName(enrollment.getUser().getFullName())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .enrolledAt(enrollment.getEnrolledAt())
                .status(enrollment.getStatus())
                .build();
    }
}
