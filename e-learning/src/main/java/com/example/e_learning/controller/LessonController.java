package com.example.e_learning.controller;

import com.example.e_learning.dto.request.LessonReorderRequest;
import com.example.e_learning.dto.request.LessonRequest;
import com.example.e_learning.dto.response.LessonResponse;
import com.example.e_learning.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/api/v1/sections/{sectionId}/lessons")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<LessonResponse> createLesson(@PathVariable Long sectionId,
                                                       @Valid @RequestBody LessonRequest request) {
        LessonResponse created = lessonService.createLesson(sectionId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/sections/{sectionId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessonsBySectionId(@PathVariable Long sectionId) {
        return ResponseEntity.ok(lessonService.getLessonsBySectionId(sectionId));
    }

    @GetMapping("/api/v1/lessons/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @PutMapping("/api/v1/lessons/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<LessonResponse> updateLesson(@PathVariable Long id,
                                                       @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @DeleteMapping("/api/v1/lessons/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/sections/{sectionId}/lessons/reorder")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<LessonResponse>> reorderLessons(@PathVariable Long sectionId,
                                                                @Valid @RequestBody LessonReorderRequest request) {
        return ResponseEntity.ok(lessonService.reorderLessons(sectionId, request));
    }
}

