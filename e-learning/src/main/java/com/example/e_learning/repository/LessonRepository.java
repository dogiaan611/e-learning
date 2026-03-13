package com.example.e_learning.repository;

import com.example.e_learning.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findBySectionIdOrderByPositionAsc(Long sectionId);

    int countBySectionId(Long sectionId);

    List<Lesson> findBySectionIdAndPositionGreaterThanOrderByPositionAsc(Long sectionId, Integer position);
}
