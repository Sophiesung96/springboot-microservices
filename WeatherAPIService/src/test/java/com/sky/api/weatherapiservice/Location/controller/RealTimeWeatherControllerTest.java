package com.sky.api.weatherapiservice.Location.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import com.sky.api.weatherapiservice.Location.repository.RealTimeWeatherRepository;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.Location.service.RealTimeWeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = RealTimeWeatherController.class)
@AutoConfigureMockMvc
@Import(SecurityConfigForTestControllerTests.class)
@ActiveProfiles("test")
public class RealTimeWeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeoLocationService geoLocationService;

    @MockitoBean()
    private RealTimeWeatherService realTimeWeatherService;

    private static final String END_POINT_PATH = "/v1/realtime";

    @Autowired
    ObjectMapper objectMapper;





    @Test
    public void testGetShouldReturn400BadRequest() throws Exception {
        // Mock the service to throw GeoLocationException
        Mockito.when(geoLocationService.getLocation(Mockito.anyString())).thenThrow(new GeoLocationException("GeoLocation error"));

        // Perform the request and verify the response
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();


        System.out.println(mvcResult.getResponse().getContentAsString());
    }


    @Test
    public void testGetShouldReturn404NotFound() throws Exception {

        Mockito.when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(new Location());
        // Mock the service to throw RealtimeException
        Mockito.when(geoLocationService.getLocation(Mockito.anyString())).thenThrow(new LocationNotFoundException("Location not found"));

        // Perform the request and verify the response
        MvcResult mvcResult = mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetShouldReturn200Ok() throws Exception {
        // Mock the location object
        Location location = Location.builder()
                .code("SFCA_USA")
                .cityName("San Francisco")
                .regionName("California")
                .countryName("United States Of America")
                .countryCode("US")
                .build();

        // Mock the RealTimeWeather object
        RealTimeWeather realTimeWeather = RealTimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .lastUpdated(new Date())
                .precipitation(12)
                .status("Cloudy")
                .windSpeed(5)
                .location(location)
                .build();

        // Link the objects
        location.setRealTimeWeather(realTimeWeather);

        // Mock service behavior
        Mockito.when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(realTimeWeatherService.getByLocation(location)).thenReturn(realTimeWeather);

        // Perform the test
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.temperature").value(12))
                .andExpect(jsonPath("$.humidity").value(32))
                .andExpect(jsonPath("$.status").value("Cloudy"))
                .andDo(print());
    }



    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "Test";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        RealTimeWeather realTimeWeather = RealTimeWeather.builder()
                .locationCode(locationCode)
                .temperature(112)
                .humidity(332)
                .lastUpdated(new Date())
                .precipitation(300)
                .status("Cloudy")
                .windSpeed(5)
                .build();

        Mockito.when(realTimeWeatherService.update(Mockito.eq(locationCode), Mockito.any(RealTimeWeather.class)))
                .thenThrow(new LocationNotFoundException("No location found with the given location code"));


        // Perform PUT request and verify
        mockMvc.perform(put(requestURI)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(realTimeWeather)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }




}





