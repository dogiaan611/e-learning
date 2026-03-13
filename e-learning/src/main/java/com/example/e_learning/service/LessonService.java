package com.example.e_learning.service;

import com.example.e_learning.dto.request.LessonReorderRequest;
import com.example.e_learning.dto.request.LessonRequest;
import com.example.e_learning.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {

    LessonResponse createLesson(Long sectionId, LessonRequest request);

    List<LessonResponse> getLessonsBySectionId(Long sectionId);

    LessonResponse getLessonById(Long id);

    LessonResponse updateLesson(Long id, LessonRequest request);

    void deleteLesson(Long id);

    List<LessonResponse> reorderLessons(Long sectionId, LessonReorderRequest request);
}

