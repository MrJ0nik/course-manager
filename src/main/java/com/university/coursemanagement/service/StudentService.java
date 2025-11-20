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
        if (studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new IllegalArgumentException("Student with ID " + dto.getStudentId() + " already exists");
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + dto.getEmail() + " already exists");
        }

        Student student = Student.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .studentId(dto.getStudentId())
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
        Student student = studentRepository.findByStudentId(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with code: " + studentCode));
        return toDTO(student);
    }

    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student student = getStudentEntity(id);

        if (!student.getStudentId().equals(dto.getStudentId()) && studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new IllegalArgumentException("Student with ID " + dto.getStudentId() + " already exists");
        }
        if (!student.getEmail().equals(dto.getEmail()) && studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + dto.getEmail() + " already exists");
        }

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setStudentId(dto.getStudentId());

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
        Course course = courseService.getCourseEntity(courseId);
        Student student = getStudentEntity(studentId);

        if (!course.getStudents().contains(student)) {
            course.getStudents().add(student);
            // Явного збереження немає, як у вихідній версії
        }
    }

    @Transactional
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        Course course = courseService.getCourseEntity(courseId);
        Student student = getStudentEntity(studentId);

        course.getStudents().remove(student);
        // Явного збереження немає, як у вихідній версії
    }

    @Transactional(readOnly = true)
    public Student getStudentEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Student getStudentEntityByCode(String studentCode) {
        return studentRepository.findByStudentId(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with code: " + studentCode));
    }

    private StudentDTO toDTO(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .studentId(student.getStudentId())
                .build();
    }
}