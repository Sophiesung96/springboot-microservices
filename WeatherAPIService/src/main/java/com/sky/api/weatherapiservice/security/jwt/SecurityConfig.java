//package com.sky.api.weatherapiservice.security.jwt;
//
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.intercept.AuthorizationFilter;
//
//@Configuration
//@EnableWebSecurity(debug = true)
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    @Autowired
//    JwtTokenFilter filter;
//
//    @Bean
//    PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    DaoAuthenticationProvider authenticationConfigurer(){
//        DaoAuthenticationProvider authProvider=new DaoAuthenticationProvider();
//        authProvider.setPasswordEncoder(passwordEncoder());
//        authProvider.setUserDetailsService(userDetailsService);
//        return authProvider;
//    }
//
//    private final CustomUserDetailService userDetailsService;
//
//
//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
//
//        http.authorizeHttpRequests(
//                        auth -> auth.requestMatchers("/api/oauth/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyAuthority("read","write")
//                                .requestMatchers(HttpMethod.POST, "/api/students/**").hasAuthority("SCOPE_write")
//                                .requestMatchers(HttpMethod.PUT, "/api/students/**").hasAuthority("SCOPE_write")
//                                .anyRequest().authenticated()
//                )
//                .csrf(csrf -> csrf.disable())
//                .exceptionHandling(exh -> exh.authenticationEntryPoint(
//                        (request, response, exception) -> {
//                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
//                        }))
//                .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class)
//        ;
//
//        return http.build();
//    }
//}
