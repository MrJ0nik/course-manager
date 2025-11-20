package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.ExamDTO;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Exam;
import com.university.coursemanagement.entity.CourseFormula;
import com.university.coursemanagement.exception.InvalidGradingFormulaException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final CourseService courseService;

    @Transactional
    public ExamDTO createExam(Long courseId, ExamDTO dto) {
        Course course = courseService.getCourseEntity(courseId);

        CourseFormula formula = course.getFormula();
        if (formula == null) {
            throw new InvalidGradingFormulaException("Grading formula must be set before creating exams");
        }

        List<Exam> existingExams = examRepository.findByCourseId(courseId);
        int totalExamPoints = existingExams.stream()
                .mapToInt(Exam::getMaxPoints)
                .sum() + dto.getMaxPoints();

        if (totalExamPoints > formula.getExamPoints()) {
            throw new InvalidGradingFormulaException(
                    String.format("Total exam points (%d) exceeds formula limit (%d)",
                            totalExamPoints, formula.getExamPoints()));
        }

        Exam exam = Exam.builder()
                .course(course)
                .title(dto.getTitle())
                .maxPoints(dto.getMaxPoints())
                .examDate(dto.getExamDate())
                .build();

        exam = examRepository.save(exam);
        return toDTO(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamDTO> getExamsByCourseId(Long courseId) {
        return examRepository.findByCourseId(courseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamDTO getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        return toDTO(exam);
    }

    @Transactional
    public ExamDTO updateExam(Long id, ExamDTO dto) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));

        exam.setTitle(dto.getTitle());
        exam.setMaxPoints(dto.getMaxPoints());
        exam.setExamDate(dto.getExamDate());

        exam = examRepository.save(exam);
        return toDTO(exam);
    }

    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Exam getExamEntity(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
    }

    private ExamDTO toDTO(Exam exam) {
        return ExamDTO.builder()
                .id(exam.getId())
                .courseId(exam.getCourse().getId())
                .title(exam.getTitle())
                .maxPoints(exam.getMaxPoints())
                .examDate(exam.getExamDate())
                .build();
    }
}


