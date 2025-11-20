package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE (g.assignment.course.id = :courseId OR g.exam.course.id = :courseId)")
    List<Grade> findByCourseId(@Param("courseId") Long courseId);
    
    List<Grade> findByAssignmentId(Long assignmentId);
    List<Grade> findByExamId(Long examId);
}

