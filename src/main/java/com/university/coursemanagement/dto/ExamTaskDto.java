package com.university.coursemanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamTaskDto {

    @NotBlank(message = "Question is required")
    private String question;

    @NotNull(message = "Points is required")
    @Min(1)
    private Integer points;
}

