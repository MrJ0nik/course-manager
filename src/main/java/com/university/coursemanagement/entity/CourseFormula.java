package com.university.coursemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseFormula {

    @Column(nullable = false)
    private Integer numberOfLabs;

    @Column(nullable = false)
    private Integer pointsPerLab;

    @Column(nullable = false)
    private Integer examPoints;
}

