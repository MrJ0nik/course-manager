package com.university.coursemanagement.controller;

import com.university.coursemanagement.dto.GradeDTO;
import com.university.coursemanagement.dto.StudentDTO;
import com.university.coursemanagement.service.GradeService;
import com.university.coursemanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final GradeService gradeService;

    @PostMapping("/students")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO dto) {
        StudentDTO created = studentService.createStudent(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/id/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable("id") Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    // ПОВЕРНУТО ДО СТАРОГО: Конфліктний, але, за вашими словами, робочий маршрут
    @GetMapping("/students/{studentCode}")
    public ResponseEntity<StudentDTO> getStudentByCode(@PathVariable("studentCode") String studentCode) {
        StudentDTO student = studentService.getStudentByCode(studentCode);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable("id") Long id,
            @Valid @RequestBody StudentDTO dto) {
        StudentDTO updated = studentService.updateStudent(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/courses/{courseId}/students/{studentId}")
    public ResponseEntity<Void> addStudentToCourse(
            @PathVariable("courseId") Long courseId,
            @PathVariable("studentId") Long studentId) {
        studentService.addStudentToCourse(courseId, studentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/courses/{courseId}/students/{studentId}")
    public ResponseEntity<Void> removeStudentFromCourse(
            @PathVariable("courseId") Long courseId,
            @PathVariable("studentId") Long studentId) {
        studentService.removeStudentFromCourse(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/{id}/grades")
    public ResponseEntity<List<GradeDTO>> getStudentGrades(@PathVariable("id") Long id) {
        List<GradeDTO> grades = gradeService.getGradesByStudentId(id);
        return ResponseEntity.ok(grades);
    }
}