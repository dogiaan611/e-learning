package com.example.e_learning.service.impl;

import com.example.e_learning.dto.response.WishlistResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Course;
import com.example.e_learning.model.User;
import com.example.e_learning.model.Wishlist;
import com.example.e_learning.repository.CourseRepository;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.repository.WishlistRepository;
import com.example.e_learning.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public WishlistResponse addToWishlist(String userEmail, Long courseId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Check for duplicate
        if (wishlistRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new RuntimeException("Course is already in your wishlist");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .course(course)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void removeFromWishlist(String userEmail, Long courseId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Wishlist wishlist = wishlistRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist", "courseId", courseId));

        wishlistRepository.delete(wishlist);
    }

    @Override
    public Page<WishlistResponse> getWishlist(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return wishlistRepository.findByUserId(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        Course course = wishlist.getCourse();
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .course(WishlistResponse.CourseInfo.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .thumbnailUrl(course.getThumbnailUrl())
                        .price(course.getPrice())
                        .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                        .averageRating(course.getAverageRating())
                        .build())
                .build();
    }
}

