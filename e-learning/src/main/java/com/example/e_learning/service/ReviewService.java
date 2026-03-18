package com.example.e_learning.service;

import com.example.e_learning.dto.request.ReviewRequest;
import com.example.e_learning.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createOrUpdateReview(Long courseId, String userEmail, ReviewRequest request);

    Page<ReviewResponse> getReviewsByCourse(Long courseId, Pageable pageable);
}

