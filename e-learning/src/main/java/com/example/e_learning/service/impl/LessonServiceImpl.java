package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.LessonReorderRequest;
import com.example.e_learning.dto.request.LessonRequest;
import com.example.e_learning.dto.response.LessonResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Lesson;
import com.example.e_learning.model.Section;
import com.example.e_learning.repository.LessonRepository;
import com.example.e_learning.repository.SectionRepository;
import com.example.e_learning.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;

    @Override
    @Transactional
    public LessonResponse createLesson(Long sectionId, LessonRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", sectionId));

        int currentCount = lessonRepository.countBySectionId(sectionId);
        int newPosition = currentCount + 1;

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .videoUrl(request.getVideoUrl())
                .content(request.getContent())
                .duration(request.getDuration())
                .position(newPosition)
                .isFree(request.getIsFree() != null ? request.getIsFree() : false)
                .section(section)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        return mapToResponse(savedLesson);
    }

    @Override
    public List<LessonResponse> getLessonsBySectionId(Long sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section", "id", sectionId);
        }

        return lessonRepository.findBySectionIdOrderByPositionAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        return mapToResponse(lesson);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());
        lesson.setDuration(request.getDuration());
        lesson.setIsFree(request.getIsFree() != null ? request.getIsFree() : false);

        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToResponse(updatedLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        Long sectionId = lesson.getSection().getId();
        Integer deletedPosition = lesson.getPosition();

        lessonRepository.delete(lesson);

        // Lấy tất cả lesson có position lớn hơn lesson vừa xóa và giảm position đi 1
        List<Lesson> lessonsToReposition = lessonRepository
                .findBySectionIdAndPositionGreaterThanOrderByPositionAsc(sectionId, deletedPosition);

        for (Lesson l : lessonsToReposition) {
            l.setPosition(l.getPosition() - 1);
        }
        lessonRepository.saveAll(lessonsToReposition);
    }

    @Override
    @Transactional
    public List<LessonResponse> reorderLessons(Long sectionId, LessonReorderRequest request) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section", "id", sectionId);
        }

        List<Long> lessonIds = request.getLessonIds();

        for (int i = 0; i < lessonIds.size(); i++) {
            Long lessonId = lessonIds.get(i);
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

            // Đảm bảo lesson thuộc đúng section
            if (!lesson.getSection().getId().equals(sectionId)) {
                throw new RuntimeException("Lesson with id " + lessonId
                        + " does not belong to section with id " + sectionId);
            }

            lesson.setPosition(i + 1);
            lessonRepository.save(lesson);
        }

        return lessonRepository.findBySectionIdOrderByPositionAsc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .videoUrl(lesson.getVideoUrl())
                .content(lesson.getContent())
                .duration(lesson.getDuration())
                .position(lesson.getPosition())
                .isFree(lesson.getIsFree())
                .sectionId(lesson.getSection().getId())
                .build();
    }
}

