package com.sky.api.weatherapiservice.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("production")
public class ResourceServerConfig {

    private static final String LOCATION_ENDPOINT="/v1/locations";
    private static final String REALTIME_ENDPOINT="/v1/realtime";
    private static final String HOURLY_ENDPOINT="/v1/hourly";
    private static final String DAILY_ENDPOINT="/v1/daily";
    private static final String FULL_ENDPOINT="/v1/full";
    private static final String SCOPE_READER="SCOPE_READER";
    private static final String SCOPE_SYSTEM="SCOPE_SYSTEM";
    private static final String SCOPE_UPDATER="SCOPE_UPDATER";

    @Bean
    SecurityFilterChain securityFilterChainResourceServer(HttpSecurity http) throws Exception {

        http.csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2-> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers("/").permitAll()
                                .requestMatchers(HttpMethod.GET,LOCATION_ENDPOINT)
                                .hasAnyAuthority(SCOPE_READER,SCOPE_SYSTEM)
                                .requestMatchers(HttpMethod.POST,LOCATION_ENDPOINT)
                                .hasAuthority(SCOPE_SYSTEM)
                                .requestMatchers(HttpMethod.PUT,LOCATION_ENDPOINT)
                                .hasAnyAuthority(SCOPE_SYSTEM,SCOPE_UPDATER)
                                .requestMatchers(HttpMethod.DELETE,LOCATION_ENDPOINT)
                                .hasAuthority(SCOPE_SYSTEM)
                                .requestMatchers(HttpMethod.GET,REALTIME_ENDPOINT)
                                .hasAnyAuthority(SCOPE_READER,SCOPE_SYSTEM,SCOPE_UPDATER)
                                .requestMatchers(HttpMethod.POST,REALTIME_ENDPOINT)
                                .hasAuthority(SCOPE_SYSTEM)
                                .requestMatchers(HttpMethod.PUT,REALTIME_ENDPOINT)
                                .hasAnyAuthority(SCOPE_SYSTEM,SCOPE_UPDATER)
                                .requestMatchers(HttpMethod.GET,HOURLY_ENDPOINT)
                                .hasAnyAuthority(SCOPE_READER,SCOPE_SYSTEM,SCOPE_UPDATER)
                                .requestMatchers(HttpMethod.POST,HOURLY_ENDPOINT)
                                .hasAuthority(SCOPE_SYSTEM)
                                .requestMatchers(HttpMethod.PUT,HOURLY_ENDPOINT)
                                .hasAnyAuthority(SCOPE_SYSTEM,SCOPE_UPDATER)
                                .requestMatchers(HttpMethod.GET,FULL_ENDPOINT)
                                .hasAnyAuthority(SCOPE_READER,SCOPE_SYSTEM,SCOPE_UPDATER)
                                .requestMatchers(HttpMethod.POST,FULL_ENDPOINT)
                                .hasAuthority(SCOPE_SYSTEM)
                                .requestMatchers(HttpMethod.PUT,FULL_ENDPOINT)
                                .hasAnyAuthority(SCOPE_SYSTEM,SCOPE_UPDATER)



                                .anyRequest().authenticated());
        return http.build();
    }
}
