package com.university.coursemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.coursemanagement.dto.CourseDTO;
import com.university.coursemanagement.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private com.university.coursemanagement.service.GradeService gradeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateCourse() throws Exception {
        CourseDTO courseDTO = CourseDTO.builder()
                .name("Test Course")
                .code("TC123")
                .description("Test Description")
                .instructor("Test Instructor")
                .build();

        CourseDTO createdDTO = CourseDTO.builder()
                .id(1L)
                .name("Test Course")
                .code("TC123")
                .description("Test Description")
                .instructor("Test Instructor")
                .build();

        when(courseService.createCourse(any(CourseDTO.class))).thenReturn(createdDTO);

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Course"))
                .andExpect(jsonPath("$.code").value("TC123"));
    }

    @Test
    @WithMockUser
    void testGetCourseById() throws Exception {
        CourseDTO courseDTO = CourseDTO.builder()
                .id(1L)
                .name("Test Course")
                .code("TC123")
                .build();

        when(courseService.getCourseById(1L)).thenReturn(courseDTO);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Course"));
    }

    @Test
    @WithMockUser
    void testGetAllCourses() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk());
    }
}


