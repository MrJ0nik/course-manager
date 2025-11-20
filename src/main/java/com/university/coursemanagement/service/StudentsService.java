package com.university.coursemanagement.service;

import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentsService {

    private final StudentRepository studentRepository;

    @Transactional
    public Student findOrCreateByEmail(String email, String name) {
        Optional<Student> existing = studentRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }

        Student student = Student.builder()
                .email(email)
                .name(name)
                .build();

        return studentRepository.save(student);
    }
}

