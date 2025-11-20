package com.university.coursemanagement.controller;

import com.university.coursemanagement.dto.GradingFormulaDTO;
import com.university.coursemanagement.service.GradingFormulaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/formula")
@RequiredArgsConstructor
public class GradingFormulaController {

    private final GradingFormulaService formulaService;

    @PostMapping
    public ResponseEntity<GradingFormulaDTO> createFormula(
            @PathVariable Long courseId,
            @Valid @RequestBody GradingFormulaDTO dto) {
        dto.setCourseId(courseId);
        GradingFormulaDTO created = formulaService.createFormula(courseId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GradingFormulaDTO> getFormula(@PathVariable Long courseId) {
        GradingFormulaDTO formula = formulaService.getFormulaByCourseId(courseId);
        return ResponseEntity.ok(formula);
    }

    @PutMapping
    public ResponseEntity<GradingFormulaDTO> updateFormula(
            @PathVariable Long courseId,
            @Valid @RequestBody GradingFormulaDTO dto) {
        dto.setCourseId(courseId);
        GradingFormulaDTO updated = formulaService.updateFormula(courseId, dto);
        return ResponseEntity.ok(updated);
    }
}


