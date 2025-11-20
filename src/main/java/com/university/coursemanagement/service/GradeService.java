package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.GradeBookDTO;
import com.university.coursemanagement.dto.GradeDTO;
import com.university.coursemanagement.entity.*;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentService studentService;
    private final AssignmentService assignmentService;
    private final ExamService examService;
    private final CourseService courseService;

    @Transactional
    public GradeDTO createGrade(GradeDTO dto) {
        Student student = studentService.getStudentEntity(dto.getStudentId());

        Assignment assignment = null;
        Exam exam = null;

        if (dto.getAssignmentId() != null) {
            assignment = assignmentService.getAssignmentEntity(dto.getAssignmentId());
        }

        if (dto.getExamId() != null) {
            exam = examService.getExamEntity(dto.getExamId());
        }

        if (assignment == null && exam == null) {
            throw new IllegalArgumentException("Either assignmentId or examId must be provided");
        }
        if (assignment != null && exam != null) {
            throw new IllegalArgumentException("Cannot specify both assignmentId and examId");
        }

        // Validate points
        int maxPoints = assignment != null ? assignment.getMaxPoints() : exam.getMaxPoints();
        if (dto.getPoints() > maxPoints) {
            throw new IllegalArgumentException("Points cannot exceed max points: " + maxPoints);
        }

        // Check if late
        boolean isLate = false;
        int penaltyApplied = 0;
        if (assignment != null) {
            LocalDateTime deadline = assignment.getDeadline();
            LocalDateTime submittedAt = dto.getSubmittedAt() != null ? dto.getSubmittedAt() : LocalDateTime.now();
            isLate = submittedAt.isAfter(deadline);
            if (isLate && assignment.getPenaltyPerDay() != null) {
                long daysLate = java.time.Duration.between(deadline, submittedAt).toDays();
                penaltyApplied = (int) (daysLate * assignment.getPenaltyPerDay());
            }
        }

        int finalPoints = Math.max(0, dto.getPoints() - penaltyApplied);

        Grade grade = Grade.builder()
                .student(student)
                .assignment(assignment)
                .exam(exam)
                .points(finalPoints)
                .submittedAt(dto.getSubmittedAt() != null ? dto.getSubmittedAt() : LocalDateTime.now())
                .isLate(isLate)
                .penaltyApplied(penaltyApplied)
                .build();

        grade = gradeRepository.save(grade);
        return toDTO(grade);
    }

    @Transactional(readOnly = true)
    public GradeDTO getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + id));
        return toDTO(grade);
    }

    @Transactional
    public GradeDTO updateGrade(Long id, GradeDTO dto) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + id));

        int maxPoints = grade.getAssignment() != null ?
                grade.getAssignment().getMaxPoints() : grade.getExam().getMaxPoints();

        if (dto.getPoints() > maxPoints) {
            throw new IllegalArgumentException("Points cannot exceed max points: " + maxPoints);
        }

        grade.setPoints(dto.getPoints());
        if (dto.getSubmittedAt() != null) {
            grade.setSubmittedAt(dto.getSubmittedAt());
        }

        grade = gradeRepository.save(grade);
        return toDTO(grade);
    }

    @Transactional
    public void deleteGrade(Long id) {
        if (!gradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Grade not found with id: " + id);
        }
        gradeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseId(Long courseId) {
        // Verify course exists
        courseService.getCourseEntity(courseId);
        return gradeRepository.findByCourseId(courseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GradeBookDTO getGradeBook(Long courseId) {
        Course course = courseService.getCourseEntity(courseId);
        // Get students through enrollments
        List<Student> students = course.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .collect(Collectors.toList());

        GradeBookDTO gradeBook = GradeBookDTO.builder()
                .courseId(course.getId())
                .courseName(course.getName())
                .courseCode(course.getCode())
                .studentGrades(new ArrayList<>())
                .build();

        for (Student student : students) {
            List<Grade> studentGrades = gradeRepository.findByStudentId(student.getId()).stream()
                    .filter(g -> {
                        if (g.getAssignment() != null) {
                            return g.getAssignment().getCourse().getId().equals(courseId);
                        }
                        if (g.getExam() != null) {
                            return g.getExam().getCourse().getId().equals(courseId);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            List<GradeBookDTO.AssignmentGradeDTO> assignmentGrades = new ArrayList<>();
            GradeBookDTO.ExamGradeDTO examGrade = null;

            int totalPoints = 0;

            for (Grade grade : studentGrades) {
                if (grade.getAssignment() != null) {
                    Assignment assignment = grade.getAssignment();
                    assignmentGrades.add(GradeBookDTO.AssignmentGradeDTO.builder()
                            .assignmentId(assignment.getId())
                            .assignmentTitle(assignment.getTitle())
                            .maxPoints(assignment.getMaxPoints())
                            .points(grade.getPoints())
                            .isLate(grade.getIsLate())
                            .penaltyApplied(grade.getPenaltyApplied())
                            .build());
                    totalPoints += grade.getPoints();
                } else if (grade.getExam() != null) {
                    Exam exam = grade.getExam();
                    examGrade = GradeBookDTO.ExamGradeDTO.builder()
                            .examId(exam.getId())
                            .examTitle(exam.getTitle())
                            .maxPoints(exam.getMaxPoints())
                            .points(grade.getPoints())
                            .build();
                    totalPoints += grade.getPoints();
                }
            }

            String finalGrade = calculateFinalGrade(totalPoints);

            gradeBook.getStudentGrades().add(GradeBookDTO.StudentGradeDTO.builder()
                    .studentId(student.getId())
                    .studentName(student.getName() != null ? student.getName() : student.getEmail())
                    .studentIdNumber(null) // studentId no longer exists
                    .email(student.getEmail())
                    .assignmentGrades(assignmentGrades)
                    .examGrade(examGrade)
                    .totalPoints(totalPoints)
                    .finalGrade(finalGrade)
                    .build());
        }

        return gradeBook;
    }

    private String calculateFinalGrade(int totalPoints) {
        if (totalPoints >= 90) return "A";
        if (totalPoints >= 80) return "B";
        if (totalPoints >= 70) return "C";
        if (totalPoints >= 60) return "D";
        return "F";
    }

    private GradeDTO toDTO(Grade grade) {
        return GradeDTO.builder()
                .id(grade.getId())
                .studentId(grade.getStudent().getId())
                .assignmentId(grade.getAssignment() != null ? grade.getAssignment().getId() : null)
                .examId(grade.getExam() != null ? grade.getExam().getId() : null)
                .points(grade.getPoints())
                .submittedAt(grade.getSubmittedAt())
                .isLate(grade.getIsLate())
                .penaltyApplied(grade.getPenaltyApplied())
                .build();
    }
}

