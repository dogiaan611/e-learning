package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.CourseCreateRequest;
import com.example.e_learning.dto.request.CourseUpdateRequest;
import com.example.e_learning.dto.response.CourseResponse;
import com.example.e_learning.dto.response.CourseSummaryResponse;
import com.example.e_learning.exception.InvalidCoursePublishException;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Category;
import com.example.e_learning.model.Course;
import com.example.e_learning.model.Section;
import com.example.e_learning.model.User;
import com.example.e_learning.model.enums.CourseStatus;
import com.example.e_learning.repository.CategoryRepository;
import com.example.e_learning.repository.CourseRepository;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getTeacherId()));

        Course course = Course.builder()
                .title(request.getTitle())
                .category(category)
                .teacher(teacher)
                .status(CourseStatus.DRAFT)
                .build();

        Course savedCourse = courseRepository.save(course);
        return mapToResponse(savedCourse);
    }

    @Override
    @Cacheable(value = "courses")
    public Page<CourseResponse> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return mapToResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }
        if (request.getThumbnailUrl() != null) {
            course.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getLevel() != null) {
            course.setLevel(request.getLevel());
        }

        Course updatedCourse = courseRepository.save(course);
        return mapToResponse(updatedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.ARCHIVED) {
            throw new RuntimeException("Cannot delete course with status " + course.getStatus()
                    + ". Only DRAFT or ARCHIVED courses can be deleted.");
        }

        courseRepository.delete(course);
    }

    @Override
    @Transactional
    public CourseResponse publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is already published");
        }

        // Collect all validation violations
        List<String> violations = new ArrayList<>();

        if (course.getPrice() == null || course.getPrice() < 0) {
            violations.add("Course must have a valid price (>= 0)");
        }

        if (course.getThumbnailUrl() == null || course.getThumbnailUrl().isBlank()) {
            violations.add("Course must have a thumbnail image (thumbnailUrl)");
        }

        if (course.getDescription() == null || course.getDescription().isBlank()) {
            violations.add("Course must have a description");
        }

        List<Section> sections = course.getSections();
        if (sections == null || sections.isEmpty()) {
            violations.add("Course must have at least 1 section");
        } else {
            for (Section section : sections) {
                if (section.getLessons() == null || section.getLessons().isEmpty()) {
                    violations.add("Section '" + section.getTitle() + "' (id=" + section.getId() + ") must have at least 1 lesson");
                }
            }
        }

        if (!violations.isEmpty()) {
            throw new InvalidCoursePublishException(violations);
        }

        course.setStatus(CourseStatus.PUBLISHED);
        Course publishedCourse = courseRepository.save(course);
        return mapToResponse(publishedCourse);
    }

    @Override
    @Transactional
    public CourseResponse unpublishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Only PUBLISHED courses can be unpublished. Current status: " + course.getStatus());
        }

        course.setStatus(CourseStatus.DRAFT);
        Course unpublishedCourse = courseRepository.save(course);
        return mapToResponse(unpublishedCourse);
    }

    @Override
    public Page<CourseSummaryResponse> getPublishedCourses(Long categoryId, Pageable pageable) {
        Page<Course> courses;
        if (categoryId != null) {
            courses = courseRepository.findByStatusAndCategoryId(CourseStatus.PUBLISHED, categoryId, pageable);
        } else {
            courses = courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);
        }
        return courses.map(this::mapToSummary);
    }

    @Override
    public Page<CourseSummaryResponse> searchCourses(String keyword, Pageable pageable) {
        return courseRepository.searchByKeyword(CourseStatus.PUBLISHED, keyword, pageable)
                .map(this::mapToSummary);
    }

    private CourseSummaryResponse mapToSummary(Course course) {
        return CourseSummaryResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .averageRating(course.getAverageRating())
                .totalReviews(course.getTotalReviews())
                .build();
    }

    private CourseResponse mapToResponse(Course course) {
        CourseResponse.CourseResponseBuilder builder = CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .thumbnailUrl(course.getThumbnailUrl())
                .level(course.getLevel())
                .status(course.getStatus());

        if (course.getCategory() != null) {
            builder.category(CourseResponse.CategoryInfo.builder()
                    .id(course.getCategory().getId())
                    .name(course.getCategory().getName())
                    .build());
        }

        if (course.getTeacher() != null) {
            builder.teacher(CourseResponse.TeacherInfo.builder()
                    .id(course.getTeacher().getId())
                    .fullName(course.getTeacher().getFullName())
                    .email(course.getTeacher().getEmail())
                    .build());
        }

        return builder.build();
    }
}

