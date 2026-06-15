package com.budgetmap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Solo aplica a rutas de tu API
                        .allowedOrigins(
                                "http://localhost:3000", // Típico puerto de React/Vue/Angular
                                "http://127.0.0.1:5500", // Típico puerto de Live Server (VS Code)
                                "https://www.budgetmap.com", // Tu dominio real (cuando subas a prod)
                                "https://budgetmap.com"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600); // 1 hora de caché para peticiones OPTIONS
            }
        };
    }
}