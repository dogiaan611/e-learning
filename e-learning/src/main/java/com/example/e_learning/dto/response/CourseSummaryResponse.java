package com.example.e_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryResponse {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private Double price;
    private String teacherName;
    private Double averageRating;
    private Integer totalReviews;
}

