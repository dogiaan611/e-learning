package com.example.e_learning.repository;

import com.example.e_learning.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByCourseIdOrderByPositionAsc(Long courseId);

    int countByCourseId(Long courseId);

    List<Section> findByCourseIdAndPositionGreaterThanOrderByPositionAsc(Long courseId, Integer position);
}
