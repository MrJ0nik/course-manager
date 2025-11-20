package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.AssignmentDTO;
import com.university.coursemanagement.entity.Assignment;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.GradingFormula;
import com.university.coursemanagement.exception.InvalidGradingFormulaException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseService courseService;
    private final GradingFormulaService formulaService;

    @Transactional
    public AssignmentDTO createAssignment(Long courseId, AssignmentDTO dto) {
        Course course = courseService.getCourseEntity(courseId);

        GradingFormula formula = course.getGradingFormula();
        if (formula == null) {
            throw new InvalidGradingFormulaException("Grading formula must be set before creating assignments");
        }

        List<Assignment> existingAssignments = assignmentRepository.findByCourseId(courseId);
        if (existingAssignments.size() >= formula.getAssignmentCount()) {
            throw new InvalidGradingFormulaException(
                    String.format("Cannot create more assignments. Maximum allowed: %d", formula.getAssignmentCount()));
        }

        int totalPoints = existingAssignments.stream()
                .mapToInt(Assignment::getMaxPoints)
                .sum() + dto.getMaxPoints();

        if (totalPoints > (formula.getAssignmentCount() * formula.getPointsPerAssignment())) {
            throw new InvalidGradingFormulaException(
                    String.format("Total assignment points (%d) exceeds formula limit (%d × %d = %d)",
                            totalPoints, formula.getAssignmentCount(), formula.getPointsPerAssignment(),
                            formula.getAssignmentCount() * formula.getPointsPerAssignment()));
        }

        Assignment assignment = Assignment.builder()
                .course(course)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .maxPoints(dto.getMaxPoints())
                .deadline(dto.getDeadline())
                .latePenaltyPoints(dto.getLatePenaltyPoints() != null ? dto.getLatePenaltyPoints() : 0)
                .orderNumber(dto.getOrderNumber())
                .build();

        assignment = assignmentRepository.save(assignment);
        return toDTO(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAssignmentsByCourseId(Long courseId) {
        return assignmentRepository.findByCourseId(courseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentDTO getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        return toDTO(assignment);
    }

    @Transactional
    public AssignmentDTO updateAssignment(Long id, AssignmentDTO dto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setMaxPoints(dto.getMaxPoints());
        assignment.setDeadline(dto.getDeadline());
        assignment.setLatePenaltyPoints(dto.getLatePenaltyPoints() != null ? dto.getLatePenaltyPoints() : 0);
        assignment.setOrderNumber(dto.getOrderNumber());

        assignment = assignmentRepository.save(assignment);
        return toDTO(assignment);
    }

    @Transactional
    public AssignmentDTO updateDeadline(Long id, java.time.LocalDateTime deadline) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        assignment.setDeadline(deadline);
        assignment = assignmentRepository.save(assignment);
        return toDTO(assignment);
    }

    @Transactional
    public AssignmentDTO updatePenalty(Long id, Integer penaltyPoints) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        assignment.setLatePenaltyPoints(penaltyPoints);
        assignment = assignmentRepository.save(assignment);
        return toDTO(assignment);
    }

    @Transactional
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment not found with id: " + id);
        }
        assignmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Assignment getAssignmentEntity(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
    }

    private AssignmentDTO toDTO(Assignment assignment) {
        return AssignmentDTO.builder()
                .id(assignment.getId())
                .courseId(assignment.getCourse().getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .maxPoints(assignment.getMaxPoints())
                .deadline(assignment.getDeadline())
                .latePenaltyPoints(assignment.getLatePenaltyPoints())
                .orderNumber(assignment.getOrderNumber())
                .build();
    }
}

