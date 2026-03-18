package com.example.e_learning.model;

import com.example.e_learning.model.enums.CourseLevel;
import com.example.e_learning.model.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Double averageRating = 0.0;
    private Integer totalReviews = 0;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Section> sections;
}
