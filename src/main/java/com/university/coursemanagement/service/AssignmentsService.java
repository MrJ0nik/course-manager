package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.CreateAssignmentDto;
import com.university.coursemanagement.dto.CreateExamVariantDto;
import com.university.coursemanagement.dto.CreateSubmissionDto;
import com.university.coursemanagement.dto.ExamTaskDto;
import com.university.coursemanagement.entity.*;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentsService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final ExamVariantRepository examVariantRepository;
    private final ExamTaskRepository examTaskRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;

    @Transactional
    public Assignment create(Long courseId, CreateAssignmentDto dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Validate against course formula
        validateAssignmentAgainstFormula(courseId, dto);

        Assignment assignment = Assignment.builder()
                .course(course)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(dto.getType())
                .deadline(dto.getDeadline())
                .maxPoints(dto.getMaxPoints())
                .penaltyPerDay(dto.getPenaltyPerDay())
                .build();

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment update(Long id, CreateAssignmentDto dto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // If updating points, validate against formula
        if (dto.getMaxPoints() != null) {
            CreateAssignmentDto fullDto = new CreateAssignmentDto();
            fullDto.setTitle(dto.getTitle() != null ? dto.getTitle() : assignment.getTitle());
            fullDto.setDescription(dto.getDescription() != null ? dto.getDescription() : assignment.getDescription());
            fullDto.setType(dto.getType() != null ? dto.getType() : assignment.getType());
            fullDto.setDeadline(dto.getDeadline() != null ? dto.getDeadline() : assignment.getDeadline());
            fullDto.setMaxPoints(dto.getMaxPoints());
            fullDto.setPenaltyPerDay(dto.getPenaltyPerDay() != null ? dto.getPenaltyPerDay() : assignment.getPenaltyPerDay());
            validateAssignmentAgainstFormula(assignment.getCourse().getId(), fullDto);
        }

        if (dto.getTitle() != null) assignment.setTitle(dto.getTitle());
        if (dto.getDescription() != null) assignment.setDescription(dto.getDescription());
        if (dto.getType() != null) assignment.setType(dto.getType());
        if (dto.getDeadline() != null) assignment.setDeadline(dto.getDeadline());
        if (dto.getMaxPoints() != null) assignment.setMaxPoints(dto.getMaxPoints());
        if (dto.getPenaltyPerDay() != null) assignment.setPenaltyPerDay(dto.getPenaltyPerDay());

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment addExamVariant(Long assignmentId, CreateExamVariantDto dto) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        if (assignment.getType() != Assignment.AssignmentType.EXAM) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only add variants to exam assignments");
        }

        // Validate total points match the exam points
        int totalPoints = dto.getTasks().stream()
                .mapToInt(ExamTaskDto::getPoints)
                .sum();
        if (totalPoints != assignment.getMaxPoints()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Total points in variant (%d) must match exam points (%d)", totalPoints, assignment.getMaxPoints()));
        }

        // Check if variant number already exists
        if (examVariantRepository.existsByAssignmentIdAndVariantNumber(assignmentId, dto.getVariantNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Variant number already exists");
        }

        ExamVariant variant = ExamVariant.builder()
                .assignment(assignment)
                .variantNumber(dto.getVariantNumber())
                .build();

        variant = examVariantRepository.save(variant);
        final ExamVariant finalVariant = variant;

        // Create tasks
        List<ExamTask> tasks = dto.getTasks().stream()
                .map(taskDto -> ExamTask.builder()
                        .examVariant(finalVariant)
                        .question(taskDto.getQuestion())
                        .points(taskDto.getPoints())
                        .build())
                .collect(Collectors.toList());

        examTaskRepository.saveAll(tasks);
        variant.setTasks(tasks);

        return assignmentRepository.findById(assignmentId).orElse(assignment);
    }

    @Transactional
    public Submission submitAssignment(CreateSubmissionDto dto) {
        Assignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Check if student is enrolled in the course
        if (!enrollmentRepository.existsByCourseIdAndStudentId(assignment.getCourse().getId(), student.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is not enrolled in this course");
        }

        // Calculate penalty if past deadline
        int points = assignment.getMaxPoints();
        LocalDateTime submissionDate = LocalDateTime.now();
        if (submissionDate.isAfter(assignment.getDeadline())) {
            long daysLate = java.time.Duration.between(assignment.getDeadline(), submissionDate).toDays();
            int penalty = (int) (daysLate * assignment.getPenaltyPerDay());
            points = Math.max(0, points - penalty);
        }

        Submission submission = Submission.builder()
                .student(student)
                .assignment(assignment)
                .content(dto.getContent())
                .submittedAt(submissionDate)
                .points(points)
                .penaltyApplied(false)
                .build();

        return submissionRepository.save(submission);
    }

    private void validateAssignmentAgainstFormula(Long courseId, CreateAssignmentDto dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getFormula() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course formula must be set before creating assignments");
        }

        CourseFormula formula = course.getFormula();
        List<Assignment> existingAssignments = assignmentRepository.findByCourseId(courseId);

        if (dto.getType() == Assignment.AssignmentType.LAB) {
            // Check number of labs
            long labCount = existingAssignments.stream()
                    .filter(a -> a.getType() == Assignment.AssignmentType.LAB)
                    .count();
            if (labCount >= formula.getNumberOfLabs()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot add more labs. Maximum allowed: %d", formula.getNumberOfLabs()));
            }

            // Check lab points
            if (!dto.getMaxPoints().equals(formula.getPointsPerLab())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Lab points must be exactly %d", formula.getPointsPerLab()));
            }
        }

        if (dto.getType() == Assignment.AssignmentType.EXAM) {
            // Check if exam already exists
            boolean hasExam = existingAssignments.stream()
                    .anyMatch(a -> a.getType() == Assignment.AssignmentType.EXAM);
            if (hasExam) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course already has an exam");
            }

            // Check exam points
            if (!dto.getMaxPoints().equals(formula.getExamPoints())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Exam points must be exactly %d", formula.getExamPoints()));
            }
        }
    }
}

