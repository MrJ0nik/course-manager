package com.university.coursemanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseFormulaDto {

    @Positive
    @Max(10)
    private Integer numberOfLabs;

    @Positive
    @Max(20)
    private Integer pointsPerLab;

    @Positive
    @Min(20)
    @Max(60)
    private Integer examPoints;
}

