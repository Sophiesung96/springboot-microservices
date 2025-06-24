package com.sky.api.weatherapiservice.config;

import com.sky.api.weatherapiservice.DTO.FullWeatherModelAssembler;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SpyBeanConfig {
    @Bean
    public FullWeatherModelAssembler fullWeatherModelAssemblerSpy() {

        return Mockito.spy(new FullWeatherModelAssembler());
    }
}
