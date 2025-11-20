package com.university.coursemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(JavaMailSender.class)
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendDeadlineReminder(String to, String studentName, String assignmentTitle, String deadline) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Deadline Reminder: " + assignmentTitle);
            message.setText(String.format(
                    "Dear %s,\n\n" +
                    "This is a reminder that the deadline for assignment '%s' is approaching.\n" +
                    "Deadline: %s\n\n" +
                    "Please make sure to submit your work on time.\n\n" +
                    "Best regards,\n" +
                    "Course Management System",
                    studentName, assignmentTitle, deadline
            ));

            mailSender.send(message);
            log.info("Deadline reminder email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendLateSubmissionNotification(String to, String studentName, String assignmentTitle, int penaltyPoints) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Late Submission: " + assignmentTitle);
            message.setText(String.format(
                    "Dear %s,\n\n" +
                    "Your submission for assignment '%s' was received after the deadline.\n" +
                    "Penalty applied: %d points\n\n" +
                    "Best regards,\n" +
                    "Course Management System",
                    studentName, assignmentTitle, penaltyPoints
            ));

            mailSender.send(message);
            log.info("Late submission notification sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}


