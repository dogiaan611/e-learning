package com.example.e_learning.service;

import com.example.e_learning.dto.request.CourseCreateRequest;
import com.example.e_learning.dto.request.CourseUpdateRequest;
import com.example.e_learning.dto.response.CourseResponse;
import com.example.e_learning.dto.response.CourseSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponse createCourse(CourseCreateRequest request);

    Page<CourseResponse> getAllCourses(Pageable pageable);

    CourseResponse getCourseById(Long id);

    CourseResponse updateCourse(Long id, CourseUpdateRequest request);

    void deleteCourse(Long id);

    CourseResponse publishCourse(Long id);

    CourseResponse unpublishCourse(Long id);

    Page<CourseSummaryResponse> getPublishedCourses(Long categoryId, Pageable pageable);

    Page<CourseSummaryResponse> searchCourses(String keyword, Pageable pageable);
}

