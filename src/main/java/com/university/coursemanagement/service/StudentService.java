package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.StudentDTO;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseService courseService;

    @Transactional
    public StudentDTO createStudent(StudentDTO dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + dto.getEmail() + " already exists");
        }

        // For compatibility, use firstName + lastName as name if available
        String name = (dto.getFirstName() != null && dto.getLastName() != null) 
                ? dto.getFirstName() + " " + dto.getLastName()
                : dto.getEmail();

        Student student = Student.builder()
                .email(dto.getEmail())
                .name(name)
                .build();

        student = studentRepository.save(student);
        return toDTO(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        Student student = getStudentEntity(id);
        return toDTO(student);
    }

    @Transactional(readOnly = true)
    public StudentDTO getStudentByCode(String studentCode) {
        // Try to find by email (since studentId no longer exists)
        Student student = studentRepository.findByEmail(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with code: " + studentCode));
        return toDTO(student);
    }

    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student student = getStudentEntity(id);

        if (!student.getEmail().equals(dto.getEmail()) && studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + dto.getEmail() + " already exists");
        }

        String name = (dto.getFirstName() != null && dto.getLastName() != null) 
                ? dto.getFirstName() + " " + dto.getLastName()
                : (dto.getEmail() != null ? dto.getEmail() : student.getName());
        
        student.setEmail(dto.getEmail());
        student.setName(name);

        student = studentRepository.save(student);
        return toDTO(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Transactional
    public void addStudentToCourse(Long courseId, Long studentId) {
        // This method is kept for compatibility but uses Enrollment now
        // The actual enrollment is handled by StudentsController
        // This is a no-op for now to maintain API compatibility
    }

    @Transactional
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        // This method is kept for compatibility but uses Enrollment now
        // The actual enrollment removal should be handled separately
        // This is a no-op for now to maintain API compatibility
    }

    @Transactional(readOnly = true)
    public Student getStudentEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Student getStudentEntityByCode(String studentCode) {
        return studentRepository.findByEmail(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with code: " + studentCode));
    }

    private StudentDTO toDTO(Student student) {
        // Split name into firstName and lastName for compatibility
        String[] nameParts = student.getName() != null ? student.getName().split(" ", 2) : new String[]{"", ""};
        return StudentDTO.builder()
                .id(student.getId())
                .firstName(nameParts.length > 0 ? nameParts[0] : "")
                .lastName(nameParts.length > 1 ? nameParts[1] : "")
                .email(student.getEmail())
                .studentId(null) // studentId no longer exists
                .build();
    }
}