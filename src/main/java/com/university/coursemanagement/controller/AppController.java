package com.university.coursemanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class AppController {

    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Exam-manager Spring Boot is running");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/routes")
    public ResponseEntity<Map<String, Object>> routes() {
        List<Map<String, String>> routes = Arrays.asList(
                createRoute("GET", "/"),
                createRoute("GET", "/routes"),
                createRoute("POST", "/courses"),
                createRoute("GET", "/courses"),
                createRoute("GET", "/courses/:id/journal"),
                createRoute("POST", "/assignments/:courseId"),
                createRoute("PATCH", "/assignments/:id"),
                createRoute("POST", "/students/enroll/:courseId")
        );

        Map<String, Object> response = new HashMap<>();
        response.put("routes", routes);
        return ResponseEntity.ok(response);
    }

    private Map<String, String> createRoute(String method, String path) {
        Map<String, String> route = new HashMap<>();
        route.put("method", method);
        route.put("path", path);
        return route;
    }
}

