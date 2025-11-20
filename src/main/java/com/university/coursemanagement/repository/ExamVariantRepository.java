package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.ExamVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamVariantRepository extends JpaRepository<ExamVariant, Long> {
    List<ExamVariant> findByAssignmentId(Long assignmentId);
    boolean existsByAssignmentIdAndVariantNumber(Long assignmentId, Integer variantNumber);
}


