package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.CommentRequest;
import com.example.e_learning.dto.response.CommentResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Comment;
import com.example.e_learning.model.Lesson;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.CommentRepository;
import com.example.e_learning.repository.LessonRepository;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    public Page<CommentResponse> getCommentsByLesson(Long lessonId, Pageable pageable) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson", "id", lessonId);
        }
        return commentRepository.findByLessonIdAndParentCommentIsNull(lessonId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long lessonId, String userEmail, CommentRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .lesson(lesson)
                .parentComment(null)
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse replyToComment(Long parentCommentId, String userEmail, CommentRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", parentCommentId));

        Comment reply = Comment.builder()
                .content(request.getContent())
                .user(user)
                .lesson(parentComment.getLesson())
                .parentComment(parentComment)
                .build();

        Comment saved = commentRepository.save(reply);
        return mapToResponse(saved);
    }

    private CommentResponse mapToResponse(Comment comment) {
        List<CommentResponse> replies = Collections.emptyList();
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            replies = comment.getReplies().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .user(CommentResponse.UserInfo.builder()
                        .id(comment.getUser().getId())
                        .fullName(comment.getUser().getFullName())
                        .avatarUrl(comment.getUser().getAvatarUrl())
                        .build())
                .replies(replies)
                .build();
    }
}

