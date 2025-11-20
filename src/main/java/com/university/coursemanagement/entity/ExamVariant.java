package com.university.coursemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(nullable = false)
    private Integer variantNumber;

    @OneToMany(mappedBy = "examVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExamTask> tasks = new ArrayList<>();
}


