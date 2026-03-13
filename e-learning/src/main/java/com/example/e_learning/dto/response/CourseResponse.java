package com.example.e_learning.dto.response;

import com.example.e_learning.model.enums.CourseLevel;
import com.example.e_learning.model.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private Double price;
    private String thumbnailUrl;
    private CourseLevel level;
    private CourseStatus status;

    private CategoryInfo category;
    private TeacherInfo teacher;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherInfo {
        private Long id;
        private String fullName;
        private String email;
    }
}

