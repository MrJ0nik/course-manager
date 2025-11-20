package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.StudentDTO;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.StudentRepository;
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
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private StudentService studentService;

    private StudentDTO studentDTO;
    private Student student;

    @BeforeEach
    void setUp() {
        studentDTO = StudentDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.edu")
                .studentId("ST123456")
                .build();

        student = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.edu")
                .studentId("ST123456")
                .build();
    }

    @Test
    void testCreateStudent_Success() {
        when(studentRepository.existsByStudentId("ST123456")).thenReturn(false);
        when(studentRepository.existsByEmail("john.doe@university.edu")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO result = studentService.createStudent(studentDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("ST123456", result.getStudentId());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testCreateStudent_DuplicateStudentId() {
        when(studentRepository.existsByStudentId("ST123456")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudent(studentDTO);
        });

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testGetStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentById(1L);
        });
    }

    @Test
    void testUpdateStudent_Success() {
        StudentDTO updateDTO = StudentDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("john.doe@university.edu")
                .studentId("ST123456")
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.existsByStudentId("ST123456")).thenReturn(false);
        when(studentRepository.existsByEmail("john.doe@university.edu")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO result = studentService.updateStudent(1L, updateDTO);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testDeleteStudent_Success() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            studentService.deleteStudent(1L);
        });

        verify(studentRepository, times(1)).deleteById(1L);
    }
}


