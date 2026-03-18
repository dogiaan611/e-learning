package com.example.e_learning.service;

import com.example.e_learning.dto.response.WishlistResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishlistService {

    WishlistResponse addToWishlist(String userEmail, Long courseId);

    void removeFromWishlist(String userEmail, Long courseId);

    Page<WishlistResponse> getWishlist(String userEmail, Pageable pageable);
}

