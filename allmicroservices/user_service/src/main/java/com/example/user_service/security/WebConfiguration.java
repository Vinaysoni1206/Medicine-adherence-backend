package com.example.user_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is a configuration class for authentication handler and interceptor
 */
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    @Profile("dev")
    public AuthenticationHandler authenticationHandler() {
        return new AuthenticationHandler();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authenticationHandler())
                .addPathPatterns(
//                        "/api/v1/email",
//                        "/api/v1/users",
//                        "/api/v1/request",
//                        "/api/v1/accept",
//                        "/api/v1/patients",
//                        "/api/v1/patient/requests",
//                        "/api/v1/caretakers",
  //                      "/api/v1/caretaker/requests"
                         "/api/v1/medicines/sync/**"
//                        "/api/v1/userdetails"
                );

    }

    @Override //add allow methods
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080");
    }
}
