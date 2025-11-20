package com.university.coursemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Застосувати CORS до всіх шляхів
                .allowedOrigins("http://localhost:3000") // Дозволити лише фронтенд
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Дозволити всі необхідні методи
                .allowedHeaders("*") // Дозволити всі заголовки
                .allowCredentials(true) // Дозволити креденшіали (JWT)
                .maxAge(3600); // Час кешування Preflight
    }
}