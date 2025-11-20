package com.university.coursemanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamVariantDto {

    @NotNull(message = "Variant number is required")
    @Min(1)
    private Integer variantNumber;

    @NotEmpty(message = "Tasks are required")
    @Valid
    private List<ExamTaskDto> tasks;
}

