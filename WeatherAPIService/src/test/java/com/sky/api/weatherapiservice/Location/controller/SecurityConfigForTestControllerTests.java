package com.sky.api.weatherapiservice.Location.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test")
public class SecurityConfigForTestControllerTests {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth->
                auth.anyRequest().permitAll())
                .csrf(csrf-> csrf.disable());

        return http.build();
    }
}
