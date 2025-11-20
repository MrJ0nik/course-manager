package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.GradingFormulaDTO;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.GradingFormula;
import com.university.coursemanagement.exception.InvalidGradingFormulaException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.GradingFormulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GradingFormulaService {

    private final GradingFormulaRepository formulaRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    @Transactional
    public GradingFormulaDTO createFormula(Long courseId, GradingFormulaDTO dto) {
        Course course = courseService.getCourseEntity(courseId);

        validateFormula(dto);

        GradingFormula formula = GradingFormula.builder()
                .course(course)
                .totalPoints(dto.getTotalPoints())
                .assignmentCount(dto.getAssignmentCount())
                .pointsPerAssignment(dto.getPointsPerAssignment())
                .examPoints(dto.getExamPoints())
                .description(dto.getDescription())
                .build();

        formula = formulaRepository.save(formula);
        course.setGradingFormula(formula);
        courseRepository.save(course);

        return toDTO(formula);
    }

    @Transactional(readOnly = true)
    public GradingFormulaDTO getFormulaByCourseId(Long courseId) {
        GradingFormula formula = formulaRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Grading formula not found for course: " + courseId));
        return toDTO(formula);
    }

    @Transactional
    public GradingFormulaDTO updateFormula(Long courseId, GradingFormulaDTO dto) {
        GradingFormula formula = formulaRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Grading formula not found for course: " + courseId));

        validateFormula(dto);

        formula.setTotalPoints(dto.getTotalPoints());
        formula.setAssignmentCount(dto.getAssignmentCount());
        formula.setPointsPerAssignment(dto.getPointsPerAssignment());
        formula.setExamPoints(dto.getExamPoints());
        formula.setDescription(dto.getDescription());

        formula = formulaRepository.save(formula);
        return toDTO(formula);
    }

    @Transactional(readOnly = true)
    public GradingFormula getFormulaEntity(Long courseId) {
        return formulaRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Grading formula not found for course: " + courseId));
    }

    private void validateFormula(GradingFormulaDTO dto) {
        int calculatedTotal = (dto.getAssignmentCount() * dto.getPointsPerAssignment()) + dto.getExamPoints();

        if (calculatedTotal != dto.getTotalPoints()) {
            throw new InvalidGradingFormulaException(
                    String.format("Formula validation failed: %d assignments × %d points + %d exam points = %d, but total is %d",
                            dto.getAssignmentCount(), dto.getPointsPerAssignment(), dto.getExamPoints(),
                            calculatedTotal, dto.getTotalPoints()));
        }

        if (dto.getTotalPoints() != 100) {
            throw new InvalidGradingFormulaException("Total points must be exactly 100");
        }
    }

    private GradingFormulaDTO toDTO(GradingFormula formula) {
        return GradingFormulaDTO.builder()
                .id(formula.getId())
                .courseId(formula.getCourse().getId())
                .totalPoints(formula.getTotalPoints())
                .assignmentCount(formula.getAssignmentCount())
                .pointsPerAssignment(formula.getPointsPerAssignment())
                .examPoints(formula.getExamPoints())
                .description(formula.getDescription())
                .build();
    }
}


