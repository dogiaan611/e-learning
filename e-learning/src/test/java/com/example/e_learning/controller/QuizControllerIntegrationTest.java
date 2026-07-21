package com.example.e_learning.controller;

import com.example.e_learning.dto.request.QuizSubmitRequest;
import com.example.e_learning.dto.response.QuizResultResponse;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class QuizControllerIntegrationTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private QuizService quizService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizController quizController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizController).build();
    }

    @Test
    void submitQuiz_ShouldReturnResult() throws Exception {
        // Arrange
        QuizSubmitRequest request = new QuizSubmitRequest();
        Map<Long, Long> answers = new HashMap<>();
        answers.put(10L, 101L);
        request.setAnswers(answers);

        QuizResultResponse mockResponse = new QuizResultResponse();
        mockResponse.setScore(100.0);
        mockResponse.setCorrectAnswers(1);
        mockResponse.setTotalQuestions(1);

        when(quizService.submitQuiz(eq(5L), any(), any(QuizSubmitRequest.class))).thenReturn(mockResponse);

        User mockUser = User.builder().id(1L).email("student@test.com").build();
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(mockUser));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "student@test.com", null, null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/quizzes/5/submit")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(100.0))
                .andExpect(jsonPath("$.correctAnswers").value(1))
                .andExpect(jsonPath("$.totalQuestions").value(1));
    }
}
