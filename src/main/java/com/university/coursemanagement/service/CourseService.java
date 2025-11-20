package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.CourseDTO;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.exception.DuplicateCourseException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @CacheEvict(value = "courses", allEntries = true)
    @Transactional
    public CourseDTO createCourse(CourseDTO dto) {
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateCourseException("Course with code " + dto.getCode() + " already exists");
        }

        Course course = Course.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .instructor(dto.getInstructor())
                .build();

        course = courseRepository.save(course);
        return toDTO(course);
    }


    @Cacheable(value = "courses")
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return toDTO(course);
    }

    @Cacheable(value = "courses", key = "'all'")
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "courses", allEntries = true)
    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        if (!course.getCode().equals(dto.getCode()) && courseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateCourseException("Course with code " + dto.getCode() + " already exists");
        }

        course.setName(dto.getName());
        course.setCode(dto.getCode());
        course.setDescription(dto.getDescription());
        course.setInstructor(dto.getInstructor());

        course = courseRepository.save(course);
        return toDTO(course);
    }

    @CacheEvict(value = "courses", allEntries = true)
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Course getCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private CourseDTO toDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .code(course.getCode())
                .description(course.getDescription())
                .instructor(course.getInstructor())
                .createdAt(course.getCreatedAt())
                .build();
    }
}