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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private User testUser;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("student@test.com").build();
        testCourse = Course.builder().id(1L).title("Test Course").status(CourseStatus.PUBLISHED).build();
    }

    @Test
    void enrollCourse_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(false);
        
        Enrollment savedEnrollment = Enrollment.builder()
                .id(1L).user(testUser).course(testCourse)
                .status(EnrollmentStatus.COMPLETED).enrolledAt(LocalDateTime.now()).build();
        
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);

        // Act
        EnrollmentResponse response = enrollmentService.enrollCourse(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Course", response.getCourseTitle());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void enrollCourse_CourseNotPublished_ThrowsException() {
        // Arrange
        testCourse.setStatus(CourseStatus.DRAFT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> enrollmentService.enrollCourse(1L, 1L));
        assertTrue(exception.getMessage().contains("Cannot enroll in a course that is not published"));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void enrollCourse_AlreadyEnrolled_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> enrollmentService.enrollCourse(1L, 1L));
        assertEquals("User is already enrolled in this course", exception.getMessage());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }
}
