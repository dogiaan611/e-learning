package com.example.e_learning.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer duration; // in seconds
    private Integer position;
    private Boolean isFree = false;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
}
