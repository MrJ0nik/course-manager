package com.university.coursemanagement.dto;

import com.university.coursemanagement.entity.Assignment;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Type is required")
    private Assignment.AssignmentType type;

    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    @NotNull(message = "Max points is required")
    @Positive
    @Max(100)
    private Integer maxPoints;

    @NotNull(message = "Penalty per day is required")
    @Min(0)
    @Max(100)
    private Integer penaltyPerDay;
}

