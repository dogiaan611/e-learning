package com.example.e_learning.controller;

import com.example.e_learning.dto.request.SectionReorderRequest;
import com.example.e_learning.dto.request.SectionRequest;
import com.example.e_learning.dto.response.SectionResponse;
import com.example.e_learning.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("/api/v1/courses/{courseId}/sections")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long courseId,
                                                         @Valid @RequestBody SectionRequest request) {
        SectionResponse created = sectionService.createSection(courseId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/courses/{courseId}/sections")
    public ResponseEntity<List<SectionResponse>> getSectionsByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(sectionService.getSectionsByCourseId(courseId));
    }

    @PutMapping("/api/v1/sections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<SectionResponse> updateSection(@PathVariable Long id,
                                                         @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.updateSection(id, request));
    }

    @DeleteMapping("/api/v1/sections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/courses/{courseId}/sections/reorder")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<SectionResponse>> reorderSections(@PathVariable Long courseId,
                                                                  @Valid @RequestBody SectionReorderRequest request) {
        return ResponseEntity.ok(sectionService.reorderSections(courseId, request));
    }
}

