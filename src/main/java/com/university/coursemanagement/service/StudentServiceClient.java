package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.StudentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// WebClient for second server communication
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceClient {

    @Value("${student.service.url:http://localhost:8081}")
    private String studentServiceUrl;

    private final WebClient.Builder webClientBuilder;

    // Get students by course
    public List<StudentDTO> getStudentsByCourse(Long courseId) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(studentServiceUrl).build();
            
            return webClient.get()
                    .uri("/api/students/course/{courseId}", courseId)
                    .retrieve()
                    .bodyToFlux(StudentDTO.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.warn("Student service unavailable, falling back to local repository: {}", e.getMessage());
            return List.of();
        }
    }

    // Create student on remote server
    public StudentDTO createStudentOnRemoteServer(StudentDTO studentDTO) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(studentServiceUrl).build();
            
            return webClient.post()
                    .uri("/api/students")
                    .bodyValue(studentDTO)
                    .retrieve()
                    .bodyToMono(StudentDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to create student on remote server: {}", e.getMessage());
            throw new RuntimeException("Student service unavailable", e);
        }
    }

    // Get student grades from second server
    public List<com.university.coursemanagement.dto.GradeDTO> getStudentGrades(Long studentId) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(studentServiceUrl).build();
            
            return webClient.get()
                    .uri("/api/students/{studentId}/grades", studentId)
                    .retrieve()
                    .bodyToFlux(com.university.coursemanagement.dto.GradeDTO.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.warn("Student service unavailable: {}", e.getMessage());
            return List.of();
        }
    }
}

