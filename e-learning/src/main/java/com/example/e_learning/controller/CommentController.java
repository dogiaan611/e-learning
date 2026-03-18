package com.example.e_learning.controller;

import com.example.e_learning.dto.request.CommentRequest;
import com.example.e_learning.dto.response.CommentResponse;
import com.example.e_learning.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/v1/lessons/{lessonId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long lessonId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByLesson(lessonId, pageable));
    }

    @PostMapping("/api/v1/lessons/{lessonId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long lessonId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        CommentResponse response = commentService.createComment(lessonId, authentication.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/api/v1/comments/{commentId}/reply")
    public ResponseEntity<CommentResponse> replyToComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        CommentResponse response = commentService.replyToComment(commentId, authentication.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

