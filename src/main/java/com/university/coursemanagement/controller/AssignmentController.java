package com.university.coursemanagement.controller;

import com.university.coursemanagement.dto.AssignmentDTO;
import com.university.coursemanagement.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<AssignmentDTO> createAssignment(
            @PathVariable("courseId") Long courseId, // <-- Додано ("courseId")
            @Valid @RequestBody AssignmentDTO dto) {
        dto.setCourseId(courseId);
        AssignmentDTO created = assignmentService.createAssignment(courseId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByCourse(@PathVariable("courseId") Long courseId) { // <-- Додано
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsByCourseId(courseId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/assignments/{id}")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable("id") Long id) { // <-- Додано ("id")
        AssignmentDTO assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    @PutMapping("/assignments/{id}")
    public ResponseEntity<AssignmentDTO> updateAssignment(
            @PathVariable("id") Long id,
            @Valid @RequestBody AssignmentDTO dto) {
        AssignmentDTO updated = assignmentService.updateAssignment(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/assignments/{id}/deadline")
    public ResponseEntity<AssignmentDTO> updateDeadline(
            @PathVariable("id") Long id,
            @RequestBody LocalDateTime deadline) {
        AssignmentDTO updated = assignmentService.updateDeadline(id, deadline);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/assignments/{id}/penalty")
    public ResponseEntity<AssignmentDTO> updatePenalty(
            @PathVariable("id") Long id,
            @RequestBody Integer penaltyPoints) {
        AssignmentDTO updated = assignmentService.updatePenalty(id, penaltyPoints);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable("id") Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}