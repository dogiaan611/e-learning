package com.example.e_learning.controller;

import com.example.e_learning.dto.request.WishlistRequest;
import com.example.e_learning.dto.response.WishlistResponse;
import com.example.e_learning.service.WishlistService;
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
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistResponse> addToWishlist(
            @Valid @RequestBody WishlistRequest request,
            Authentication authentication) {
        WishlistResponse response = wishlistService.addToWishlist(authentication.getName(), request.getCourseId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long courseId,
            Authentication authentication) {
        wishlistService.removeFromWishlist(authentication.getName(), courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<WishlistResponse>> getWishlist(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(wishlistService.getWishlist(authentication.getName(), pageable));
    }
}

