package com.university.coursemanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Long assignmentId;

    private Long examId;

    @NotNull(message = "Points is required")
    @Min(value = 0, message = "Points cannot be negative")
    private Integer points;

    private LocalDateTime submittedAt;

    private Boolean isLate;

    private Integer penaltyApplied;
}


