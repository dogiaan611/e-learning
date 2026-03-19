package com.example.e_learning.repository;

import com.example.e_learning.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserId(Long userId);
    List<QuizResult> findByQuizId(Long quizId);
    Optional<QuizResult> findTopByUserIdAndQuizIdOrderByScoreDesc(Long userId, Long quizId);
    List<QuizResult> findByUserIdAndQuizId(Long userId, Long quizId);
}
