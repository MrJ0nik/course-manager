package com.university.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grading_formulas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingFormula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    private Course course;

    @Column(nullable = false)
    private Integer totalPoints; // Must be 100

    @Column(nullable = false)
    private Integer assignmentCount;

    @Column(nullable = false)
    private Integer pointsPerAssignment;

    @Column(nullable = false)
    private Integer examPoints;

    @Column(length = 500)
    private String description;
}

