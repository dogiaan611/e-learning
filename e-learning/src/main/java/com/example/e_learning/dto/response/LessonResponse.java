package com.example.e_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Long id;
    private String title;
    private String videoUrl;
    private String content;
    private Integer duration;
    private Integer position;
    private Boolean isFree;
    private Long sectionId;
}

