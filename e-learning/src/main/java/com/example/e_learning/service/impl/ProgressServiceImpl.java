package com.example.e_learning.service.impl;

import com.example.e_learning.dto.request.ProgressUpdateRequest;
import com.example.e_learning.dto.response.CourseProgressResponse;
import com.example.e_learning.dto.response.LessonProgressResponse;
import com.example.e_learning.exception.ResourceNotFoundException;
import com.example.e_learning.model.Course;
import com.example.e_learning.model.Lesson;
import com.example.e_learning.model.Progress;
import com.example.e_learning.model.Section;
import com.example.e_learning.model.User;
import com.example.e_learning.repository.CourseRepository;
import com.example.e_learning.repository.EnrollmentRepository;
import com.example.e_learning.repository.LessonRepository;
import com.example.e_learning.repository.ProgressRepository;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public LessonProgressResponse updateLessonProgress(Long userId, Long lessonId,
                                                        ProgressUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        // Kiểm tra user đã enroll khóa học chứa bài này chưa
        Long courseId = lesson.getSection().getCourse().getId();
        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        if (!enrolled) {
            throw new RuntimeException("User is not enrolled in this course");
        }

        // Tìm hoặc tạo mới bản ghi Progress
        Progress progress = progressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElse(Progress.builder()
                        .user(user)
                        .lesson(lesson)
                        .isCompleted(false)
                        .lastWatchedTime(0)
                        .build());

        // Cập nhật dữ liệu từ request
        if (request.getIsCompleted() != null) {
            progress.setIsCompleted(request.getIsCompleted());
        }
        if (request.getLastWatchedTime() != null) {
            progress.setLastWatchedTime(request.getLastWatchedTime());
        }

        Progress saved = progressRepository.save(progress);
        return mapToLessonProgressResponse(saved);
    }

    @Override
    public CourseProgressResponse getCourseProgress(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Kiểm tra user đã enroll chưa
        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        if (!enrolled) {
            throw new RuntimeException("User is not enrolled in this course");
        }

        // Lấy tất cả bài học trong khóa
        List<Lesson> allLessons = new ArrayList<>();
        for (Section section : course.getSections()) {
            allLessons.addAll(lessonRepository.findBySectionIdOrderByPositionAsc(section.getId()));
        }
        int totalLessons = allLessons.size();

        // Lấy tất cả progress của user trong khóa học
        List<Progress> progresses = progressRepository.findByUserIdAndCourseId(userId, courseId);
        Map<Long, Progress> progressMap = progresses.stream()
                .collect(Collectors.toMap(p -> p.getLesson().getId(), p -> p));

        // Tính toán tiến độ từng bài và số bài hoàn thành
        List<LessonProgressResponse> lessonProgresses = new ArrayList<>();
        int completedCount = 0;

        for (Lesson lesson : allLessons) {
            Progress p = progressMap.get(lesson.getId());
            boolean isCompleted = p != null && Boolean.TRUE.equals(p.getIsCompleted());
            if (isCompleted) completedCount++;

            lessonProgresses.add(LessonProgressResponse.builder()
                    .lessonId(lesson.getId())
                    .lessonTitle(lesson.getTitle())
                    .isCompleted(isCompleted)
                    .lastWatchedTime(p != null ? p.getLastWatchedTime() : 0)
                    .build());
        }

        // Tính phần trăm
        double progressPercent = totalLessons == 0 ? 0.0
                : Math.round((completedCount * 100.0 / totalLessons) * 10) / 10.0;

        return CourseProgressResponse.builder()
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .totalLessons(totalLessons)
                .completedLessons(completedCount)
                .progressPercent(progressPercent)
                .lessonProgresses(lessonProgresses)
                .build();
    }

    private LessonProgressResponse mapToLessonProgressResponse(Progress progress) {
        return LessonProgressResponse.builder()
                .lessonId(progress.getLesson().getId())
                .lessonTitle(progress.getLesson().getTitle())
                .isCompleted(progress.getIsCompleted())
                .lastWatchedTime(progress.getLastWatchedTime())
                .build();
    }
}
