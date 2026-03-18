package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.ReviewRequest;
import com.example.e_learning.dto.response.ReviewResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Course;
import com.example.e_learning.model.Review;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.CourseRepository;
import com.example.e_learning.repository.EnrollmentRepository;
import com.example.e_learning.repository.ReviewRepository;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public ReviewResponse createOrUpdateReview(Long courseId, String userEmail, ReviewRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Check if user is enrolled in the course
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId);
        if (!isEnrolled) {
            throw new RuntimeException("You must be enrolled in this course to leave a review");
        }

        // Check if user already reviewed this course → update existing review
        Optional<Review> existingReview = reviewRepository.findByUserIdAndCourseId(user.getId(), courseId);

        Review review;
        if (existingReview.isPresent()) {
            review = existingReview.get();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
        } else {
            review = Review.builder()
                    .user(user)
                    .course(course)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
        }

        Review savedReview = reviewRepository.save(review);

        // Update course average rating and total reviews
        updateCourseRatingStats(course);

        return mapToResponse(savedReview);
    }

    @Override
    public Page<ReviewResponse> getReviewsByCourse(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }
        return reviewRepository.findByCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    private void updateCourseRatingStats(Course course) {
        Double avgRating = reviewRepository.getAverageRatingByCourseId(course.getId());
        Integer totalReviews = reviewRepository.countByCourseId(course.getId());

        course.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        course.setTotalReviews(totalReviews != null ? totalReviews : 0);
        courseRepository.save(course);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .user(ReviewResponse.UserInfo.builder()
                        .id(review.getUser().getId())
                        .fullName(review.getUser().getFullName())
                        .avatarUrl(review.getUser().getAvatarUrl())
                        .build())
                .build();
    }
}

