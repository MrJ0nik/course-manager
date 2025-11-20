package com.university.coursemanagement.service;

import com.university.coursemanagement.entity.Assignment;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.Submission;
import com.university.coursemanagement.repository.AssignmentRepository;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.SubmissionRepository;
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
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    
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
            if (deadline == null) continue;

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
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(assignment.getCourse().getId());
        
        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();
            // Check if student already submitted
            boolean hasSubmission = submissionRepository.findByStudentIdAndAssignmentId(
                    student.getId(), assignment.getId()).stream()
                    .findAny()
                    .isPresent();

            if (!hasSubmission && emailService != null) {
                String deadlineStr = assignment.getDeadline()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                
                emailService.sendDeadlineReminder(
                        student.getEmail(),
                        student.getName(),
                        assignment.getTitle(),
                        deadlineStr
                );
            }
        }
    }

    private void applyLatePenalties(Assignment assignment) {
        if (assignment.getPenaltyPerDay() == null || assignment.getPenaltyPerDay() == 0) {
            return;
        }

        List<Submission> submissions = submissionRepository.findByAssignmentId(assignment.getId());

        for (Submission submission : submissions) {
            if (!submission.getPenaltyApplied() && submission.getSubmittedAt().isAfter(assignment.getDeadline())) {
                long daysLate = java.time.Duration.between(assignment.getDeadline(), submission.getSubmittedAt()).toDays();
                if (daysLate > 0) {
                    int penalty = (int) (daysLate * assignment.getPenaltyPerDay());
                    submission.setPenaltyApplied(true);
                    int newPoints = Math.max(0, submission.getPoints() - penalty);
                    submission.setPoints(newPoints);

                    submissionRepository.save(submission);

                    // Send notification
                    if (emailService != null) {
                        Student student = submission.getStudent();
                        emailService.sendLateSubmissionNotification(
                                student.getEmail(),
                                student.getName(),
                                assignment.getTitle(),
                                penalty
                        );
                    }
                }
            }
        }
    }
}


