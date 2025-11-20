package com.university.coursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubmissionDto {

    @NotNull(message = "Student ID is required")
    @Positive
    private Long studentId;

    @NotNull(message = "Assignment ID is required")
    @Positive
    private Long assignmentId;

    @NotBlank(message = "Content is required")
    private String content;
}

