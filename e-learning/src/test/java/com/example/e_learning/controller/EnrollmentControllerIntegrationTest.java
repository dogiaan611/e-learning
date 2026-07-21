package com.example.e_learning.controller;

import com.example.e_learning.dto.response.EnrollmentResponse;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
        User mockUser = User.builder().id(1L).email("student@test.com").build();
    }

    @Test
    void enrollCourse_ShouldReturnCreated() throws Exception {
        // Arrange
        EnrollmentResponse mockResponse = EnrollmentResponse.builder()
                .id(10L)
                .courseTitle("Advanced Java")
                .build();

        when(enrollmentService.enrollCourse(any(), eq(99L))).thenReturn(mockResponse);
        
        User mockUser = User.builder().id(1L).email("student@test.com").build();
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "student@test.com", null, null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/enrollments/courses/99")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.courseTitle").value("Advanced Java"));
    }
}
