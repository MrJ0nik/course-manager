package com.university.coursemanagement.controller;

import com.university.coursemanagement.dto.EnrollStudentDto;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.service.StudentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentsController {

    private final StudentsService studentsService;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<Map<String, Object>> enroll(
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody EnrollStudentDto dto) {
        
        Student student = studentsService.findOrCreateByEmail(dto.getEmail(), dto.getName());
        
        // Create enrollment if it doesn't exist
        if (!enrollmentRepository.existsByCourseIdAndStudentId(courseId, student.getId())) {
            Enrollment enrollment = Enrollment.builder()
                    .course(courseRepository.findById(courseId).orElseThrow())
                    .student(student)
                    .build();
            enrollmentRepository.save(enrollment);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("courseId", courseId);
        response.put("student", student);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

