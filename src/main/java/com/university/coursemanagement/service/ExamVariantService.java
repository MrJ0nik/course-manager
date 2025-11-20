package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.ExamVariantDTO;
import com.university.coursemanagement.entity.Exam;
import com.university.coursemanagement.entity.ExamVariant;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.ExamRepository;
import com.university.coursemanagement.repository.ExamVariantRepository;
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

    @Transactional
    public ExamVariantDTO createVariant(Long examId, ExamVariantDTO dto) {
        Exam exam = examService.getExamEntity(examId);

        ExamVariant variant = ExamVariant.builder()
                .exam(exam)
                .variantNumber(dto.getVariantNumber())
                .questions(dto.getQuestions() != null ? dto.getQuestions() : new java.util.ArrayList<>())
                .build();

        variant = variantRepository.save(variant);
        return toDTO(variant);
    }

    @Transactional(readOnly = true)
    public List<ExamVariantDTO> getVariantsByExamId(Long examId) {
        return variantRepository.findByExamId(examId).stream()
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

        if (!variant.getExam().getId().equals(examId)) {
            throw new ResourceNotFoundException("Exam variant does not belong to the specified exam");
        }

        variant.setVariantNumber(dto.getVariantNumber());
        variant.setQuestions(dto.getQuestions() != null ? dto.getQuestions() : new java.util.ArrayList<>());

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
        return ExamVariantDTO.builder()
                .id(variant.getId())
                .examId(variant.getExam().getId())
                .variantNumber(variant.getVariantNumber())
                .questions(variant.getQuestions())
                .build();
    }
}


