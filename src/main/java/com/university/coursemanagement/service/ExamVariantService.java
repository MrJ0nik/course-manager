package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.ExamVariantDTO;
import com.university.coursemanagement.entity.Assignment;
import com.university.coursemanagement.entity.Exam;
import com.university.coursemanagement.entity.ExamTask;
import com.university.coursemanagement.entity.ExamVariant;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.ExamRepository;
import com.university.coursemanagement.repository.ExamVariantRepository;
import com.university.coursemanagement.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamVariantService {

    private final ExamVariantRepository variantRepository;
    private final ExamService examService;
    private final AssignmentService assignmentService;

    @Transactional
    public ExamVariantDTO createVariant(Long examId, ExamVariantDTO dto) {
        // For compatibility: if examId is provided, try to get exam, otherwise treat as assignmentId
        Assignment assignment = null;
        try {
            Exam exam = examService.getExamEntity(examId);
            // If exam exists, we need to find corresponding assignment
            // For now, we'll use assignmentId directly if provided in DTO
            if (dto.getExamId() != null) {
                // This is legacy code path - exam variants are now assignment-based
                throw new ResourceNotFoundException("Exam variants are now assignment-based. Use assignment ID instead.");
            }
        } catch (ResourceNotFoundException e) {
            // Treat examId as assignmentId for new structure
            assignment = assignmentService.getAssignmentEntity(examId);
        }

        ExamVariant variant = ExamVariant.builder()
                .assignment(assignment)
                .variantNumber(dto.getVariantNumber())
                .build();

        variant = variantRepository.save(variant);
        return toDTO(variant);
    }

    @Transactional(readOnly = true)
    public List<ExamVariantDTO> getVariantsByExamId(Long examId) {
        // For compatibility: treat examId as assignmentId
        return variantRepository.findByAssignmentId(examId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamVariantDTO getVariantById(Long id) {
        ExamVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam variant not found with id: " + id));
        return toDTO(variant);
    }

    @Transactional
    public ExamVariantDTO updateVariant(Long examId, Long variantId, ExamVariantDTO dto) {
        ExamVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam variant not found with id: " + variantId));

        if (variant.getAssignment() == null || !variant.getAssignment().getId().equals(examId)) {
            throw new ResourceNotFoundException("Exam variant does not belong to the specified assignment");
        }

        variant.setVariantNumber(dto.getVariantNumber());
        // Questions are now tasks - handled separately

        variant = variantRepository.save(variant);
        return toDTO(variant);
    }

    @Transactional
    public void deleteVariant(Long id) {
        if (!variantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exam variant not found with id: " + id);
        }
        variantRepository.deleteById(id);
    }

    private ExamVariantDTO toDTO(ExamVariant variant) {
        Long assignmentId = variant.getAssignment() != null ? variant.getAssignment().getId() : null;
        // Convert tasks to questions for compatibility
        List<String> questions = variant.getTasks() != null 
                ? variant.getTasks().stream().map(ExamTask::getQuestion).collect(Collectors.toList())
                : new java.util.ArrayList<>();
        
        return ExamVariantDTO.builder()
                .id(variant.getId())
                .examId(assignmentId) // For compatibility, use assignmentId as examId
                .variantNumber(variant.getVariantNumber())
                .questions(questions)
                .build();
    }
}


