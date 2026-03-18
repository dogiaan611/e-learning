package com.example.e_learning.repository;

import com.example.e_learning.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Fetch root comments (parentComment is null) for a lesson, with replies eagerly loaded
    @Query("SELECT DISTINCT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.lesson.id = :lessonId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findRootCommentsWithReplies(@Param("lessonId") Long lessonId);

    Page<Comment> findByLessonIdAndParentCommentIsNull(Long lessonId, Pageable pageable);
}

