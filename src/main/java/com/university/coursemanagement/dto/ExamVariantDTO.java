package com.university.coursemanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamVariantDTO {

    private Long id;

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Variant number is required")
    @Min(value = 1, message = "Variant number must be at least 1")
    private Integer variantNumber;

    @Builder.Default
    private List<String> questions = new ArrayList<>();
}


