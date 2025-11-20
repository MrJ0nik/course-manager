package com.university.coursemanagement.controller;

import com.university.coursemanagement.dto.CreateCourseDto;
import com.university.coursemanagement.dto.CourseFormulaDto;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.Submission;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.repository.SubmissionRepository;
import com.university.coursemanagement.service.CoursesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CoursesController {

    private final CoursesService coursesService;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SubmissionRepository submissionRepository;

    @PostMapping
    public ResponseEntity<Course> create(@Valid @RequestBody CreateCourseDto dto) {
        Course course = coursesService.create(dto);
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Course>> findAll() {
        List<Course> courses = coursesService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}/journal")
    public ResponseEntity<Map<String, Object>> getJournal(@PathVariable("id") Long id) {
        Course course = coursesService.findById(id);
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(id);
        
        List<Map<String, Object>> journal = enrollments.stream()
                .map(enrollment -> {
                    Student student = enrollment.getStudent();
                    List<Submission> submissions = submissionRepository.findByStudentId(student.getId());
                    
                    // Filter submissions for this course
                    List<Submission> courseSubmissions = submissions.stream()
                            .filter(s -> s.getAssignment().getCourse().getId().equals(id))
                            .collect(Collectors.toList());
                    
                    int totalPoints = courseSubmissions.stream()
                            .mapToInt(Submission::getPoints)
                            .sum();
                    
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("studentId", student.getId());
                    entry.put("studentName", student.getName());
                    entry.put("totalPoints", totalPoints);
                    return entry;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("course", course);
        response.put("journal", journal);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/formula")
    public ResponseEntity<Course> setFormula(
            @PathVariable("id") Long id,
            @Valid @RequestBody CourseFormulaDto dto) {
        Course course = coursesService.setFormula(id, dto);
        return ResponseEntity.ok(course);
    }
}

