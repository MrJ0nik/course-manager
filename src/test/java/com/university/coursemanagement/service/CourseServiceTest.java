package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.CourseDTO;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.exception.DuplicateCourseException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CourseRepository;
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
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private CourseDTO courseDTO;
    private Course course;

    @BeforeEach
    void setUp() {
        courseDTO = CourseDTO.builder()
                .name("Test Course")
                .code("TC123")
                .description("Test Description")
                .instructor("Test Instructor")
                .build();

        course = Course.builder()
                .id(1L)
                .name("Test Course")
                .code("TC123")
                .description("Test Description")
                .instructor("Test Instructor")
                .build();
    }

    @Test
    void testCreateCourse_Success() {
        when(courseRepository.existsByCode("TC123")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDTO result = courseService.createCourse(courseDTO);

        assertNotNull(result);
        assertEquals("Test Course", result.getName());
        assertEquals("TC123", result.getCode());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testCreateCourse_DuplicateCode() {
        when(courseRepository.existsByCode("TC123")).thenReturn(true);

        assertThrows(DuplicateCourseException.class, () -> {
            courseService.createCourse(courseDTO);
        });

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testGetCourseById_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDTO result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Course", result.getName());
    }

    @Test
    void testGetCourseById_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.getCourseById(1L);
        });
    }

    @Test
    void testUpdateCourse_Success() {
        CourseDTO updateDTO = CourseDTO.builder()
                .name("Updated Course")
                .code("TC123")
                .description("Updated Description")
                .instructor("Updated Instructor")
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.existsByCode("TC123")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDTO result = courseService.updateCourse(1L, updateDTO);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testDeleteCourse_Success() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            courseService.deleteCourse(1L);
        });

        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCourse_NotFound() {
        when(courseRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.deleteCourse(1L);
        });

        verify(courseRepository, never()).deleteById(anyLong());
    }
}


