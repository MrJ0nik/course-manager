package com.university.coursemanagement.controller;

import com.university.coursemanagement.dto.ExamDTO;
import com.university.coursemanagement.dto.ExamVariantDTO;
import com.university.coursemanagement.service.ExamService;
import com.university.coursemanagement.service.ExamVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final ExamVariantService variantService;

    @PostMapping("/courses/{courseId}/exams")
    public ResponseEntity<ExamDTO> createExam(
            @PathVariable("courseId") Long courseId, // <-- Додано ("courseId")
            @Valid @RequestBody ExamDTO dto) {
        dto.setCourseId(courseId);
        ExamDTO created = examService.createExam(courseId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/courses/{courseId}/exams")
    public ResponseEntity<List<ExamDTO>> getExamsByCourse(@PathVariable("courseId") Long courseId) { // <-- Додано
        List<ExamDTO> exams = examService.getExamsByCourseId(courseId);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<ExamDTO> getExamById(@PathVariable("id") Long id) { // <-- Додано ("id")
        ExamDTO exam = examService.getExamById(id);
        return ResponseEntity.ok(exam);
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<ExamDTO> updateExam(
            @PathVariable("id") Long id,
            @Valid @RequestBody ExamDTO dto) {
        ExamDTO updated = examService.updateExam(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable("id") Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exams/{examId}/variants")
    public ResponseEntity<ExamVariantDTO> createVariant(
            @PathVariable("examId") Long examId, // <-- Додано ("examId")
            @Valid @RequestBody ExamVariantDTO dto) {
        dto.setExamId(examId);
        ExamVariantDTO created = variantService.createVariant(examId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/exams/{examId}/variants")
    public ResponseEntity<List<ExamVariantDTO>> getVariantsByExam(@PathVariable("examId") Long examId) {
        List<ExamVariantDTO> variants = variantService.getVariantsByExamId(examId);
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/variants/{id}")
    public ResponseEntity<ExamVariantDTO> getVariantById(@PathVariable("id") Long id) {
        ExamVariantDTO variant = variantService.getVariantById(id);
        return ResponseEntity.ok(variant);
    }

    @PutMapping("/exams/{examId}/variants/{variantId}")
    public ResponseEntity<ExamVariantDTO> updateVariant(
            @PathVariable("examId") Long examId,
            @PathVariable("variantId") Long variantId,
            @Valid @RequestBody ExamVariantDTO dto) {
        ExamVariantDTO updated = variantService.updateVariant(examId, variantId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/variants/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable("id") Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.noContent().build();
    }
}