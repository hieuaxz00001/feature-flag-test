package com.example.demo2;

import org.ff4j.FF4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FF4jConfig {
    @Bean
    public FF4j ff4j() {
        return new FF4j();
    }
}