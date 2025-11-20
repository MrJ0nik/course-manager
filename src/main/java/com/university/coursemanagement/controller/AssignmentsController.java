package com.university.coursemanagement.controller;

import com.university.coursemanagement.dto.CreateAssignmentDto;
import com.university.coursemanagement.dto.CreateExamVariantDto;
import com.university.coursemanagement.dto.CreateSubmissionDto;
import com.university.coursemanagement.entity.Assignment;
import com.university.coursemanagement.entity.Submission;
import com.university.coursemanagement.service.AssignmentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentsController {

    private final AssignmentsService assignmentsService;

    @PostMapping("/{courseId}")
    public ResponseEntity<Assignment> create(
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody CreateAssignmentDto dto) {
        Assignment assignment = assignmentsService.create(courseId, dto);
        return new ResponseEntity<>(assignment, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Assignment> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateAssignmentDto dto) {
        Assignment assignment = assignmentsService.update(id, dto);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{id}/variants")
    public ResponseEntity<Assignment> addExamVariant(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateExamVariantDto dto) {
        Assignment assignment = assignmentsService.addExamVariant(id, dto);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/submit")
    public ResponseEntity<Submission> submit(@Valid @RequestBody CreateSubmissionDto dto) {
        Submission submission = assignmentsService.submitAssignment(dto);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }
}

