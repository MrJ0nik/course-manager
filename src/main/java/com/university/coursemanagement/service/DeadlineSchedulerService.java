package com.university.coursemanagement.service;

import com.university.coursemanagement.entity.Assignment;
import com.university.coursemanagement.entity.Grade;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.repository.AssignmentRepository;
import com.university.coursemanagement.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineSchedulerService {

    private final AssignmentRepository assignmentRepository;
    private final GradeRepository gradeRepository;
    
    @Autowired(required = false)
    private EmailService emailService;

    @Scheduled(cron = "0 0 9 * * *") // Every day at 9 AM
    @Transactional
    public void checkDeadlinesAndSendReminders() {
        log.info("Starting deadline check and reminder sending...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Assignment> assignments = assignmentRepository.findAll();

        for (Assignment assignment : assignments) {
            LocalDateTime deadline = assignment.getDeadline();

            // Send reminder if deadline is tomorrow
            if (deadline.isAfter(now) && deadline.isBefore(tomorrow)) {
                sendRemindersForAssignment(assignment);
            }

            // Apply penalties for late submissions
            if (deadline.isBefore(now)) {
                applyLatePenalties(assignment);
            }
        }

        log.info("Deadline check completed");
    }

    private void sendRemindersForAssignment(Assignment assignment) {
        List<Student> students = assignment.getCourse().getStudents();
        
        for (Student student : students) {
            // Check if student already submitted
            boolean hasSubmission = gradeRepository.findByStudentId(student.getId()).stream()
                    .anyMatch(g -> g.getAssignment() != null && 
                            g.getAssignment().getId().equals(assignment.getId()));

            if (!hasSubmission && emailService != null) {
                String deadlineStr = assignment.getDeadline()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                
                emailService.sendDeadlineReminder(
                        student.getEmail(),
                        student.getFirstName() + " " + student.getLastName(),
                        assignment.getTitle(),
                        deadlineStr
                );
            }
        }
    }

    private void applyLatePenalties(Assignment assignment) {
        List<Grade> grades = gradeRepository.findByAssignmentId(assignment.getId());

        for (Grade grade : grades) {
            if (!grade.getIsLate() && grade.getSubmittedAt().isAfter(assignment.getDeadline())) {
                grade.setIsLate(true);
                grade.setPenaltyApplied(assignment.getLatePenaltyPoints());
                
                int newPoints = Math.max(0, grade.getPoints() - assignment.getLatePenaltyPoints());
                grade.setPoints(newPoints);

                gradeRepository.save(grade);

                // Send notification
                if (emailService != null) {
                    Student student = grade.getStudent();
                    emailService.sendLateSubmissionNotification(
                            student.getEmail(),
                            student.getFirstName() + " " + student.getLastName(),
                            assignment.getTitle(),
                            assignment.getLatePenaltyPoints()
                    );
                }
            }
        }
    }
}


