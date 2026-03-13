package com.example.e_learning.controller;

import com.example.e_learning.dto.request.CourseCreateRequest;
import com.example.e_learning.dto.request.CourseUpdateRequest;
import com.example.e_learning.dto.response.CourseResponse;
import com.example.e_learning.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        CourseResponse created = courseService.createCourse(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getAllCourses(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id,
                                                       @Valid @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.publishCourse(id));
    }

    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CourseResponse> unpublishCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.unpublishCourse(id));
    }
}

