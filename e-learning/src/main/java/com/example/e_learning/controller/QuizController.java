package com.example.e_learning.controller;

import com.example.e_learning.dto.request.QuestionRequest;
import com.example.e_learning.dto.request.QuizRequest;
import com.example.e_learning.dto.request.QuizSubmitRequest;
import com.example.e_learning.dto.response.QuizResponse;
import com.example.e_learning.dto.response.QuizResultResponse;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final UserRepository userRepository;

    // =====================================
    // QUIZ MANAGEMENT (Instructor/Admin)
    // =====================================

    /**
     * Tạo quiz mới cho một lesson
     * POST /api/v1/quizzes
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        return new ResponseEntity<>(quizService.createQuiz(request), HttpStatus.CREATED);
    }

    /**
     * Lấy chi tiết quiz (kèm đáp án đúng - chỉ dành cho Instructor/Admin)
     * GET /api/v1/quizzes/{quizId}
     */
    @GetMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizById(quizId));
    }

    /**
     * Lấy danh sách quiz theo lesson
     * GET /api/v1/quizzes/lessons/{lessonId}
     */
    @GetMapping("/lessons/{lessonId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuizResponse>> getQuizzesByLesson(@PathVariable Long lessonId,
                                                                  Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        boolean isInstructorOrAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER") || a.getAuthority().equals("ROLE_ADMIN"));

        if (isInstructorOrAdmin) {
            return ResponseEntity.ok(quizService.getQuizzesByLesson(lessonId));
        } else {
            // Student: trả về danh sách quiz không có isCorrect
            List<QuizResponse> quizzes = quizService.getQuizzesByLesson(lessonId).stream()
                    .peek(q -> {
                        if (q.getQuestions() != null) {
                            q.getQuestions().forEach(question -> {
                                if (question.getAnswers() != null) {
                                    question.getAnswers().forEach(a -> a.setIsCorrect(null));
                                }
                            });
                        }
                    })
                    .toList();
            return ResponseEntity.ok(quizzes);
        }
    }

    /**
     * Cập nhật quiz
     * PUT /api/v1/quizzes/{quizId}
     */
    @PutMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long quizId,
                                                    @Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, request));
    }

    /**
     * Xóa quiz
     * DELETE /api/v1/quizzes/{quizId}
     */
    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    // =====================================
    // QUESTION MANAGEMENT (Instructor/Admin)
    // =====================================

    /**
     * Thêm câu hỏi (kèm các đáp án) vào quiz
     * POST /api/v1/quizzes/{quizId}/questions
     */
    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<QuizResponse> addQuestion(@PathVariable Long quizId,
                                                     @Valid @RequestBody QuestionRequest request) {
        return new ResponseEntity<>(quizService.addQuestion(quizId, request), HttpStatus.CREATED);
    }

    /**
     * Cập nhật câu hỏi trong quiz
     * PUT /api/v1/quizzes/{quizId}/questions/{questionId}
     */
    @PutMapping("/{quizId}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<QuizResponse> updateQuestion(@PathVariable Long quizId,
                                                        @PathVariable Long questionId,
                                                        @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(quizService.updateQuestion(quizId, questionId, request));
    }

    /**
     * Xóa câu hỏi khỏi quiz
     * DELETE /api/v1/quizzes/{quizId}/questions/{questionId}
     */
    @DeleteMapping("/{quizId}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long quizId,
                                                @PathVariable Long questionId) {
        quizService.deleteQuestion(quizId, questionId);
        return ResponseEntity.noContent().build();
    }

    // =====================================
    // STUDENT - TAKE QUIZ
    // =====================================

    /**
     * Học viên lấy quiz để làm bài (không có isCorrect)
     * GET /api/v1/quizzes/{quizId}/take
     */
    @GetMapping("/{quizId}/take")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizResponse> getQuizForStudent(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizForStudent(quizId));
    }

    /**
     * Học viên nộp bài thi
     * POST /api/v1/quizzes/{quizId}/submit
     */
    @PostMapping("/{quizId}/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizResultResponse> submitQuiz(@PathVariable Long quizId,
                                                          @Valid @RequestBody QuizSubmitRequest request,
                                                          Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return new ResponseEntity<>(quizService.submitQuiz(quizId, user.getId(), request), HttpStatus.CREATED);
    }

    // =====================================
    // RESULTS
    // =====================================

    /**
     * Lấy điểm cao nhất của bản thân trong một quiz
     * GET /api/v1/quizzes/{quizId}/results/best
     */
    @GetMapping("/{quizId}/results/best")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizResultResponse> getBestResult(@PathVariable Long quizId,
                                                             Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(quizService.getBestResult(quizId, user.getId()));
    }

    /**
     * Lấy tất cả lần nộp bài của bản thân trong một quiz
     * GET /api/v1/quizzes/{quizId}/results/my
     */
    @GetMapping("/{quizId}/results/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuizResultResponse>> getMyResultsByQuiz(@PathVariable Long quizId,
                                                                        Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(quizService.getMyResultsByQuiz(quizId, user.getId()));
    }

    /**
     * Lấy toàn bộ kết quả quiz của bản thân (tất cả các quiz)
     * GET /api/v1/quizzes/results/my
     */
    @GetMapping("/results/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuizResultResponse>> getAllMyResults(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(quizService.getAllMyResults(user.getId()));
    }

    // =====================================
    // HELPER
    // =====================================

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
