package com.university.coursemanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingFormulaDTO {

    private Long id;

    private Long courseId;

    @NotNull(message = "Total points is required")
    @Min(value = 1, message = "Total points must be at least 1")
    @Max(value = 100, message = "Total points must be exactly 100")
    private Integer totalPoints;

    @NotNull(message = "Assignment count is required")
    @Min(value = 0, message = "Assignment count cannot be negative")
    private Integer assignmentCount;

    @NotNull(message = "Points per assignment is required")
    @Min(value = 0, message = "Points per assignment cannot be negative")
    private Integer pointsPerAssignment;

    @NotNull(message = "Exam points is required")
    @Min(value = 0, message = "Exam points cannot be negative")
    private Integer examPoints;

    private String description;
}