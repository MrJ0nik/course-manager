package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.ExamTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamTaskRepository extends JpaRepository<ExamTask, Long> {
}

