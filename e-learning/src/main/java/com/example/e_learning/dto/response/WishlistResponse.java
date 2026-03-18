package com.example.e_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private Long id;
    private CourseInfo course;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseInfo {
        private Long id;
        private String title;
        private String thumbnailUrl;
        private Double price;
        private String teacherName;
        private Double averageRating;
    }
}

