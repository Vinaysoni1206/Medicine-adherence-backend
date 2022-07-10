package com.example.user_service.security;

import com.example.user_service.config.filter.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityLayer extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailService userDetailService;



    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}