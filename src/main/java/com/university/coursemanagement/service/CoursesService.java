package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.CreateCourseDto;
import com.university.coursemanagement.dto.CourseFormulaDto;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.CourseFormula;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoursesService {

    private final CourseRepository courseRepository;

    @Transactional
    public Course create(CreateCourseDto dto) {
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Course with this code already exists");
        }
        
        Course course = Course.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    @Transactional
    public Course setFormula(Long courseId, CourseFormulaDto dto) {
        Course course = findById(courseId);
        CourseFormula formula = CourseFormula.builder()
                .numberOfLabs(dto.getNumberOfLabs())
                .pointsPerLab(dto.getPointsPerLab())
                .examPoints(dto.getExamPoints())
                .build();
        course.setFormula(formula);
        return courseRepository.save(course);
    }
}

