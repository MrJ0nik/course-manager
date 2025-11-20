package com.university.coursemanagement.service;

import com.university.coursemanagement.entity.Assignment;
import com.university.coursemanagement.entity.Submission;
import com.university.coursemanagement.repository.AssignmentRepository;
import com.university.coursemanagement.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    @Scheduled(cron = "* * * * *") // Every minute (for testing, change to appropriate schedule)
    @Transactional
    public void handleCron() {
        log.debug("Running scheduled penalty job");
        LocalDateTime now = LocalDateTime.now();
        
        List<Assignment> assignments = assignmentRepository.findAll();
        
        for (Assignment assignment : assignments) {
            if (assignment.getDeadline() == null || assignment.getPenaltyPerDay() == null) {
                continue;
            }
            
            LocalDateTime deadline = assignment.getDeadline();
            if (deadline.isBefore(now) && assignment.getPenaltyPerDay() > 0) {
                List<Submission> submissions = submissionRepository.findByAssignmentId(assignment.getId());
                
                for (Submission submission : submissions) {
                    if (!submission.getPenaltyApplied()) {
                        LocalDateTime submittedAt = submission.getSubmittedAt();
                        if (submittedAt.isAfter(deadline)) {
                            long daysLate = java.time.Duration.between(deadline, submittedAt).toDays();
                            if (daysLate > 0) {
                                int penalty = (int) (daysLate * assignment.getPenaltyPerDay());
                                submission.setPoints(Math.max(0, submission.getPoints() - penalty));
                                submission.setPenaltyApplied(true);
                                submissionRepository.save(submission);
                            }
                        }
                    }
                }
            }
        }
    }
}

