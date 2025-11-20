package com.university.coursemanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class AssignmentDTO {

    private Long id;

    // 👇 БУЛО: @NotNull(message = "Course ID is required")
    // 👇 ТРЕБА ЗРОБИТИ ТАК (видаліть цей рядок):
    private Long courseId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Max points is required")
    @Min(value = 1, message = "Max points must be at least 1")
    private Integer maxPoints;

    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    @Min(value = 0, message = "Late penalty points cannot be negative")
    private Integer latePenaltyPoints;

    @NotNull(message = "Order number is required")
    @Min(value = 1, message = "Order number must be at least 1")
    private Integer orderNumber;
}