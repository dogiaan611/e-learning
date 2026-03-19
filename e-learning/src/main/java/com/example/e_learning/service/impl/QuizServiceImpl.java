package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.AnswerRequest;
import com.example.e_learning.dto.request.QuestionRequest;
import com.example.e_learning.dto.request.QuizRequest;
import com.example.e_learning.dto.request.QuizSubmitRequest;
import com.example.e_learning.dto.response.AnswerResponse;
import com.example.e_learning.dto.response.QuestionResponse;
import com.example.e_learning.dto.response.QuizResponse;
import com.example.e_learning.dto.response.QuizResultResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.*;
import com.example.e_learning.repository.*;
import com.example.e_learning.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizResultRepository quizResultRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    // ===========================
    // QUIZ MANAGEMENT
    // ===========================

    @Override
    @Transactional
    public QuizResponse createQuiz(QuizRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", request.getLessonId()));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .lesson(lesson)
                .build();

        Quiz saved = quizRepository.save(quiz);
        return mapToResponse(saved, true);
    }

    @Override
    public QuizResponse getQuizById(Long quizId) {
        Quiz quiz = findQuizById(quizId);
        return mapToResponse(quiz, true);
    }

    @Override
    public List<QuizResponse> getQuizzesByLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson", "id", lessonId);
        }
        return quizRepository.findByLessonId(lessonId).stream()
                .map(q -> mapToResponse(q, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long quizId, QuizRequest request) {
        Quiz quiz = findQuizById(quizId);
        quiz.setTitle(request.getTitle());

        if (request.getLessonId() != null && !request.getLessonId().equals(quiz.getLesson().getId())) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", request.getLessonId()));
            quiz.setLesson(lesson);
        }

        Quiz saved = quizRepository.save(quiz);
        return mapToResponse(saved, true);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz", "id", quizId);
        }
        quizRepository.deleteById(quizId);
    }

    // ===========================
    // QUESTION MANAGEMENT
    // ===========================

    @Override
    @Transactional
    public QuizResponse addQuestion(Long quizId, QuestionRequest request) {
        Quiz quiz = findQuizById(quizId);

        validateAnswers(request);

        Question question = Question.builder()
                .content(request.getContent())
                .quiz(quiz)
                .build();

        Question savedQuestion = questionRepository.save(question);

        List<Answer> answers = buildAnswers(request.getAnswers(), savedQuestion);
        answerRepository.saveAll(answers);
        savedQuestion.setAnswers(answers);

        // Reload để đảm bảo dữ liệu đầy đủ
        Quiz reloaded = findQuizById(quizId);
        return mapToResponse(reloaded, true);
    }

    @Override
    @Transactional
    public QuizResponse updateQuestion(Long quizId, Long questionId, QuestionRequest request) {
        findQuizById(quizId); // validate quiz exists
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getQuiz().getId().equals(quizId)) {
            throw new RuntimeException("Question does not belong to the specified quiz");
        }

        validateAnswers(request);

        question.setContent(request.getContent());

        // Xóa đáp án cũ và tạo lại
        answerRepository.deleteAll(question.getAnswers());
        List<Answer> newAnswers = buildAnswers(request.getAnswers(), question);
        answerRepository.saveAll(newAnswers);
        question.setAnswers(newAnswers);

        questionRepository.save(question);

        Quiz reloaded = findQuizById(quizId);
        return mapToResponse(reloaded, true);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long quizId, Long questionId) {
        findQuizById(quizId); // validate quiz exists
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getQuiz().getId().equals(quizId)) {
            throw new RuntimeException("Question does not belong to the specified quiz");
        }

        questionRepository.delete(question);
    }

    // ===========================
    // STUDENT - TAKE QUIZ
    // ===========================

    @Override
    public QuizResponse getQuizForStudent(Long quizId) {
        Quiz quiz = findQuizById(quizId);
        // Ẩn isCorrect khi trả về cho student
        return mapToResponse(quiz, false);
    }

    @Override
    @Transactional
    public QuizResultResponse submitQuiz(Long quizId, Long userId, QuizSubmitRequest request) {
        Quiz quiz = findQuizById(quizId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Question> questions = quiz.getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("This quiz has no questions yet");
        }

        Map<Long, Long> studentAnswers = request.getAnswers();
        int correctCount = 0;
        int totalQuestions = questions.size();

        for (Question question : questions) {
            Long chosenAnswerId = studentAnswers.get(question.getId());
            if (chosenAnswerId == null) continue; // Bỏ qua câu chưa trả lời

            // Tìm đáp án student chọn và kiểm tra
            boolean isCorrect = question.getAnswers().stream()
                    .filter(a -> a.getId().equals(chosenAnswerId))
                    .findFirst()
                    .map(Answer::getIsCorrect)
                    .orElse(false);

            if (Boolean.TRUE.equals(isCorrect)) {
                correctCount++;
            }
        }

        double score = (double) correctCount / totalQuestions * 100.0;
        score = Math.round(score * 100.0) / 100.0; // Làm tròn 2 chữ số thập phân

        QuizResult result = QuizResult.builder()
                .user(user)
                .quiz(quiz)
                .score(score)
                .build();

        QuizResult saved = quizResultRepository.save(result);
        return mapResultToResponse(saved, correctCount, totalQuestions);
    }

    // ===========================
    // RESULTS
    // ===========================

    @Override
    public QuizResultResponse getBestResult(Long quizId, Long userId) {
        QuizResult result = quizResultRepository
                .findTopByUserIdAndQuizIdOrderByScoreDesc(userId, quizId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizResult", "userId/quizId", userId + "/" + quizId));

        Quiz quiz = findQuizById(quizId);
        int totalQuestions = quiz.getQuestions() == null ? 0 : quiz.getQuestions().size();
        int correctAnswers = (int) Math.round(result.getScore() * totalQuestions / 100.0);
        return mapResultToResponse(result, correctAnswers, totalQuestions);
    }

    @Override
    public List<QuizResultResponse> getMyResultsByQuiz(Long quizId, Long userId) {
        Quiz quiz = findQuizById(quizId);
        int totalQuestions = quiz.getQuestions() == null ? 0 : quiz.getQuestions().size();

        return quizResultRepository.findByUserIdAndQuizId(userId, quizId).stream()
                .map(r -> {
                    int correct = (int) Math.round(r.getScore() * totalQuestions / 100.0);
                    return mapResultToResponse(r, correct, totalQuestions);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizResultResponse> getAllMyResults(Long userId) {
        return quizResultRepository.findByUserId(userId).stream()
                .map(r -> {
                    int total = r.getQuiz().getQuestions() == null ? 0 : r.getQuiz().getQuestions().size();
                    int correct = (int) Math.round(r.getScore() * total / 100.0);
                    return mapResultToResponse(r, correct, total);
                })
                .collect(Collectors.toList());
    }

    // ===========================
    // HELPER METHODS
    // ===========================

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));
    }

    private void validateAnswers(QuestionRequest request) {
        long correctCount = request.getAnswers().stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                .count();
        if (correctCount == 0) {
            throw new RuntimeException("Question must have at least one correct answer");
        }
    }

    private List<Answer> buildAnswers(List<AnswerRequest> answerRequests, Question question) {
        return answerRequests.stream()
                .map(ar -> Answer.builder()
                        .content(ar.getContent())
                        .isCorrect(ar.getIsCorrect())
                        .question(question)
                        .build())
                .collect(Collectors.toList());
    }

    private QuizResponse mapToResponse(Quiz quiz, boolean includeCorrect) {
        List<QuestionResponse> questionResponses = null;
        if (quiz.getQuestions() != null) {
            questionResponses = quiz.getQuestions().stream()
                    .map(q -> mapQuestionToResponse(q, includeCorrect))
                    .collect(Collectors.toList());
        }

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .lessonId(quiz.getLesson() != null ? quiz.getLesson().getId() : null)
                .lessonTitle(quiz.getLesson() != null ? quiz.getLesson().getTitle() : null)
                .totalQuestions(questionResponses != null ? questionResponses.size() : 0)
                .questions(questionResponses)
                .build();
    }

    private QuestionResponse mapQuestionToResponse(Question question, boolean includeCorrect) {
        List<AnswerResponse> answerResponses = null;
        if (question.getAnswers() != null) {
            answerResponses = question.getAnswers().stream()
                    .map(a -> AnswerResponse.builder()
                            .id(a.getId())
                            .content(a.getContent())
                            .isCorrect(includeCorrect ? a.getIsCorrect() : null)
                            .build())
                    .collect(Collectors.toList());
        }

        return QuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .answers(answerResponses)
                .build();
    }

    private QuizResultResponse mapResultToResponse(QuizResult result, int correctAnswers, int totalQuestions) {
        return QuizResultResponse.builder()
                .id(result.getId())
                .userId(result.getUser().getId())
                .userFullName(result.getUser().getFullName())
                .quizId(result.getQuiz().getId())
                .quizTitle(result.getQuiz().getTitle())
                .score(result.getScore())
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .completedAt(result.getCompletedAt())
                .passed(result.getScore() != null && result.getScore() >= 50.0)
                .build();
    }
}
