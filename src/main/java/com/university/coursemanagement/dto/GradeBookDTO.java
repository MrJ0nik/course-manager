package com.university.coursemanagement.dto;

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
public class GradeBookDTO {

    private Long courseId;
    private String courseName;
    private String courseCode;

    @Builder.Default
    private List<StudentGradeDTO> studentGrades = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentGradeDTO {
        private Long studentId;
        private String studentName;
        private String studentIdNumber;
        private String email;

        @Builder.Default
        private List<AssignmentGradeDTO> assignmentGrades = new ArrayList<>();

        private ExamGradeDTO examGrade;

        private Integer totalPoints;
        private String finalGrade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignmentGradeDTO {
        private Long assignmentId;
        private String assignmentTitle;
        private Integer maxPoints;
        private Integer points;
        private Boolean isLate;
        private Integer penaltyApplied;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExamGradeDTO {
        private Long examId;
        private String examTitle;
        private Integer maxPoints;
        private Integer points;
    }
}


