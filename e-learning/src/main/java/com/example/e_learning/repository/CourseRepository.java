package com.example.e_learning.repository;

import com.example.e_learning.model.Course;
import com.example.e_learning.model.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    Page<Course> findByStatusAndCategoryId(CourseStatus status, Long categoryId, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.status = :status " +
            "AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchByKeyword(@Param("status") CourseStatus status,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);
}
