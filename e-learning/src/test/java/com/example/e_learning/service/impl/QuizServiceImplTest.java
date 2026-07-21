package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.QuizSubmitRequest;
import com.example.e_learning.dto.response.QuizResultResponse;
import com.example.e_learning.model.Answer;
import com.example.e_learning.model.Question;
import com.example.e_learning.model.Quiz;
import com.example.e_learning.model.QuizResult;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.QuizRepository;
import com.example.e_learning.repository.QuizResultRepository;
import com.example.e_learning.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizResultRepository quizResultRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    private User testUser;
    private Quiz testQuiz;
    private Question question1;
    private Question question2;
    private Answer q1CorrectAnswer;
    private Answer q1WrongAnswer;
    private Answer q2CorrectAnswer;
    private Answer q2WrongAnswer;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("student@test.com").build();
        
        testQuiz = Quiz.builder().id(1L).title("Test Quiz").build();

        q1CorrectAnswer = Answer.builder().id(101L).content("A").isCorrect(true).build();
        q1WrongAnswer = Answer.builder().id(102L).content("B").isCorrect(false).build();
        question1 = Question.builder().id(10L).content("Q1").answers(Arrays.asList(q1CorrectAnswer, q1WrongAnswer)).build();

        q2CorrectAnswer = Answer.builder().id(201L).content("A").isCorrect(true).build();
        q2WrongAnswer = Answer.builder().id(202L).content("B").isCorrect(false).build();
        question2 = Question.builder().id(20L).content("Q2").answers(Arrays.asList(q2CorrectAnswer, q2WrongAnswer)).build();

        testQuiz.setQuestions(Arrays.asList(question1, question2));
    }

    @Test
    void submitQuiz_AllCorrect_Score100() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        QuizSubmitRequest request = new QuizSubmitRequest();
        Map<Long, Long> answers = new HashMap<>();
        answers.put(10L, 101L); // Q1: Correct
        answers.put(20L, 201L); // Q2: Correct
        request.setAnswers(answers);

        QuizResult savedResult = QuizResult.builder().id(1L).user(testUser).quiz(testQuiz).score(100.0).build();
        when(quizResultRepository.save(any(QuizResult.class))).thenReturn(savedResult);

        // Act
        QuizResultResponse response = quizService.submitQuiz(1L, 1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(100.0, response.getScore());
        assertEquals(2, response.getCorrectAnswers());
        assertEquals(2, response.getTotalQuestions());
        verify(quizResultRepository, times(1)).save(any(QuizResult.class));
    }

    @Test
    void submitQuiz_HalfCorrect_Score50() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        QuizSubmitRequest request = new QuizSubmitRequest();
        Map<Long, Long> answers = new HashMap<>();
        answers.put(10L, 101L); // Q1: Correct
        answers.put(20L, 202L); // Q2: Wrong
        request.setAnswers(answers);

        QuizResult savedResult = QuizResult.builder().id(2L).user(testUser).quiz(testQuiz).score(50.0).build();
        when(quizResultRepository.save(any(QuizResult.class))).thenReturn(savedResult);

        // Act
        QuizResultResponse response = quizService.submitQuiz(1L, 1L, request);

        // Assert
        assertEquals(50.0, response.getScore());
        assertEquals(1, response.getCorrectAnswers());
        assertEquals(2, response.getTotalQuestions());
    }
}
