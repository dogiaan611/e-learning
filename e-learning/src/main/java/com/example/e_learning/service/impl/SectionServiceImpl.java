package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.SectionReorderRequest;
import com.example.e_learning.dto.request.SectionRequest;
import com.example.e_learning.dto.response.SectionResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Course;
import com.example.e_learning.model.Section;
import com.example.e_learning.repository.CourseRepository;
import com.example.e_learning.repository.SectionRepository;
import com.example.e_learning.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public SectionResponse createSection(Long courseId, SectionRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        int currentCount = sectionRepository.countByCourseId(courseId);
        int newPosition = currentCount + 1;

        Section section = Section.builder()
                .title(request.getTitle())
                .position(newPosition)
                .course(course)
                .build();

        Section savedSection = sectionRepository.save(section);
        return mapToResponse(savedSection);
    }

    @Override
    public List<SectionResponse> getSectionsByCourseId(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }

        return sectionRepository.findByCourseIdOrderByPositionAsc(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SectionResponse updateSection(Long id, SectionRequest request) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", id));

        section.setTitle(request.getTitle());

        Section updatedSection = sectionRepository.save(section);
        return mapToResponse(updatedSection);
    }

    @Override
    @Transactional
    public void deleteSection(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", id));

        Long courseId = section.getCourse().getId();
        Integer deletedPosition = section.getPosition();

        sectionRepository.delete(section);

        // Lấy tất cả section có position lớn hơn section vừa xóa và giảm position đi 1
        List<Section> sectionsToReposition = sectionRepository
                .findByCourseIdAndPositionGreaterThanOrderByPositionAsc(courseId, deletedPosition);

        for (Section s : sectionsToReposition) {
            s.setPosition(s.getPosition() - 1);
        }
        sectionRepository.saveAll(sectionsToReposition);
    }

    @Override
    @Transactional
    public List<SectionResponse> reorderSections(Long courseId, SectionReorderRequest request) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }

        List<Long> sectionIds = request.getSectionIds();

        for (int i = 0; i < sectionIds.size(); i++) {
            Long sectionId = sectionIds.get(i);
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section", "id", sectionId));

            // Đảm bảo section thuộc đúng course
            if (!section.getCourse().getId().equals(courseId)) {
                throw new RuntimeException("Section with id " + sectionId
                        + " does not belong to course with id " + courseId);
            }

            section.setPosition(i + 1);
            sectionRepository.save(section);
        }

        return sectionRepository.findByCourseIdOrderByPositionAsc(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SectionResponse mapToResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .position(section.getPosition())
                .courseId(section.getCourse().getId())
                .build();
    }
}

