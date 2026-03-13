package com.example.e_learning.service;

import com.example.e_learning.dto.request.SectionRequest;
import com.example.e_learning.dto.request.SectionReorderRequest;
import com.example.e_learning.dto.response.SectionResponse;

import java.util.List;

public interface SectionService {

    SectionResponse createSection(Long courseId, SectionRequest request);

    List<SectionResponse> getSectionsByCourseId(Long courseId);

    SectionResponse updateSection(Long id, SectionRequest request);

    void deleteSection(Long id);

    List<SectionResponse> reorderSections(Long courseId, SectionReorderRequest request);
}

