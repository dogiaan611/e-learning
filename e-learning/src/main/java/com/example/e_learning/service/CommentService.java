package com.example.e_learning.service;

import com.example.e_learning.dto.request.CommentRequest;
import com.example.e_learning.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Page<CommentResponse> getCommentsByLesson(Long lessonId, Pageable pageable);

    CommentResponse createComment(Long lessonId, String userEmail, CommentRequest request);

    CommentResponse replyToComment(Long parentCommentId, String userEmail, CommentRequest request);
}

