package com.example.e_learning.controller;

import com.example.e_learning.dto.request.ReviewRequest;
import com.example.e_learning.dto.response.ReviewResponse;
import com.example.e_learning.service.ReviewService;
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
@RequestMapping("/api/v1/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long courseId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseId, pageable));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createOrUpdateReview(
            @PathVariable Long courseId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        ReviewResponse response = reviewService.createOrUpdateReview(courseId, authentication.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

