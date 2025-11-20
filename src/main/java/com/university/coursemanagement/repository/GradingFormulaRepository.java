package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.GradingFormula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradingFormulaRepository extends JpaRepository<GradingFormula, Long> {
    Optional<GradingFormula> findByCourseId(Long courseId);
}


