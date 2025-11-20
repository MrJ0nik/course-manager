package com.university.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exam_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_variant_id", nullable = false)
    private ExamVariant examVariant;

    @Column(nullable = false, length = 1000)
    private String question;

    @Column(nullable = false)
    private Integer points;
}

