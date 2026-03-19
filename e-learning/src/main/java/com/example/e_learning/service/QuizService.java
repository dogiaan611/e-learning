package com.example.e_learning.service;

import com.example.e_learning.dto.request.QuestionRequest;
import com.example.e_learning.dto.request.QuizRequest;
import com.example.e_learning.dto.request.QuizSubmitRequest;
import com.example.e_learning.dto.response.QuizResponse;
import com.example.e_learning.dto.response.QuizResultResponse;

import java.util.List;

public interface QuizService {

    // ===== QUIZ MANAGEMENT (Instructor/Admin) =====

    /** Tạo quiz mới gắn với một lesson */
    QuizResponse createQuiz(QuizRequest request);

    /** Lấy quiz theo ID (bao gồm câu hỏi & đáp án - có isCorrect) */
    QuizResponse getQuizById(Long quizId);

    /** Lấy danh sách quiz theo lessonId */
    List<QuizResponse> getQuizzesByLesson(Long lessonId);

    /** Cập nhật tiêu đề quiz */
    QuizResponse updateQuiz(Long quizId, QuizRequest request);

    /** Xóa quiz (cascade xóa questions & answers) */
    void deleteQuiz(Long quizId);

    // ===== QUESTION MANAGEMENT (Instructor/Admin) =====

    /** Thêm câu hỏi (kèm đáp án) vào quiz */
    QuizResponse addQuestion(Long quizId, QuestionRequest request);

    /** Cập nhật câu hỏi */
    QuizResponse updateQuestion(Long quizId, Long questionId, QuestionRequest request);

    /** Xóa câu hỏi */
    void deleteQuestion(Long quizId, Long questionId);

    // ===== STUDENT - TAKE QUIZ =====

    /**
     * Lấy quiz để làm bài (KHÔNG trả về isCorrect cho student)
     */
    QuizResponse getQuizForStudent(Long quizId);

    /**
     * Student nộp bài, hệ thống chấm điểm & lưu kết quả
     */
    QuizResultResponse submitQuiz(Long quizId, Long userId, QuizSubmitRequest request);

    // ===== RESULTS =====

    /** Lấy kết quả cao nhất của user trong một quiz */
    QuizResultResponse getBestResult(Long quizId, Long userId);

    /** Lấy tất cả lần nộp bài của user trong một quiz */
    List<QuizResultResponse> getMyResultsByQuiz(Long quizId, Long userId);

    /** Lấy toàn bộ kết quả của user (mọi quiz) */
    List<QuizResultResponse> getAllMyResults(Long userId);
}
