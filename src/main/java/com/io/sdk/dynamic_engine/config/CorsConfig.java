package com.io.sdk.dynamic_engine.config;

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
                registry.addMapping("/**")
                        .allowedOriginPatterns("*") // ✅ wildcard allowed here
                        .allowedMethods("*") // ✅ all methods
                        .allowedHeaders("*") // ✅ all headers
                        .allowCredentials(true); // ✅ cookies/token allowed
            }
        };
    }
}
