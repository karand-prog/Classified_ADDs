package com.classifiedsapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				String allowed = System.getenv("CORS_ALLOWED_ORIGINS");
				if (allowed == null || allowed.isBlank()) {
					allowed = "http://localhost:3000";
				}
				registry.addMapping("/api/**")
					.allowedOriginPatterns(allowed.split(","))
					.allowCredentials(true)
					.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
				registry.addMapping("/ws/**")
					.allowedOriginPatterns(allowed.split(","))
					.allowCredentials(true)
					.allowedMethods("GET", "POST");
			}
		};
	}
} 