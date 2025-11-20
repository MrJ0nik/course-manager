package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.GradingFormulaDTO;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.GradingFormula;
import com.university.coursemanagement.exception.InvalidGradingFormulaException;
import com.university.coursemanagement.repository.GradingFormulaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradingFormulaServiceTest {

    @Mock
    private GradingFormulaRepository formulaRepository;

    @Mock
    private com.university.coursemanagement.repository.CourseRepository courseRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private GradingFormulaService formulaService;

    private Course course;
    private GradingFormulaDTO formulaDTO;

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .id(1L)
                .name("Test Course")
                .code("TC123")
                .build();

        formulaDTO = GradingFormulaDTO.builder()
                .courseId(1L)
                .totalPoints(100)
                .assignmentCount(4)
                .pointsPerAssignment(10)
                .examPoints(60)
                .description("4 labs × 10 + exam 60")
                .build();
    }

    @Test
    void testCreateFormula_Success() {
        when(courseService.getCourseEntity(1L)).thenReturn(course);
        when(formulaRepository.save(any(GradingFormula.class))).thenAnswer(invocation -> {
            GradingFormula formula = invocation.getArgument(0);
            formula.setId(1L);
            return formula;
        });
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        GradingFormulaDTO result = formulaService.createFormula(1L, formulaDTO);

        assertNotNull(result);
        assertEquals(100, result.getTotalPoints());
        verify(formulaRepository, times(1)).save(any(GradingFormula.class));
    }

    @Test
    void testCreateFormula_InvalidTotal() {
        formulaDTO.setTotalPoints(90); // Should be 100

        when(courseService.getCourseEntity(1L)).thenReturn(course);

        assertThrows(InvalidGradingFormulaException.class, () -> {
            formulaService.createFormula(1L, formulaDTO);
        });
    }

    @Test
    void testCreateFormula_InvalidCalculation() {
        formulaDTO.setPointsPerAssignment(15); // 4 × 15 + 60 = 120, not 100

        when(courseService.getCourseEntity(1L)).thenReturn(course);

        assertThrows(InvalidGradingFormulaException.class, () -> {
            formulaService.createFormula(1L, formulaDTO);
        });
    }
}

