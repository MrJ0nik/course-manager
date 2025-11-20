package com.university.coursemanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class StatusController {


    @GetMapping("/")
    public Map<String, String> getRootStatus() {
        return Map.of(
                "status", "OK",
                "message", "Course Management API is running.",
                "access", "The root path is accessible to everyone."
        );
    }}

