package com.sky.api.weatherapiservice.Location.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.DailyWeather;
import com.sky.api.weatherapicommon.entity.DailyWeatherId;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.DTO.DailyWeatherDTO;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import com.sky.api.weatherapiservice.Exception.GlobalExceptionHandler;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.DailyWeatherService;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(DailyWeatherController.class)
class DailyWeatherControllerTest {

    private static final String API_PATH="/v1/daily";
    private static final String REQUEST_CONTENT_TYPE = "application/json";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private DailyWeatherService dailyWeatherService;

    @MockitoBean
    private LocationService locationService;

    @MockitoBean
    private GeoLocationService geoLocationService;


    @Test
    public void testGetByIPShouldReturn400BadRequest() throws Exception {
        GeoLocationException geo = new GeoLocationException("GeoLocationException Error");

        Mockito.when(geoLocationService.getLocation(Mockito.any()))
                .thenThrow(geo);

        mockMvc.perform(get(API_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is(geo.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode="LACA_US";
        LocationNotFoundException ex=new LocationNotFoundException(locationCode);
        String requestURI=API_PATH+"/"+locationCode;
        Mockito.when(dailyWeatherService.getByLocationCode(locationCode)).thenThrow(ex);
        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]",is(ex.getMessage())))
                .andDo(print());
    }


    @Test
    public void testGetByCodeShouldReturn200OK() throws Exception {
        String locationCode="LDN_UK";
        Location location = Location.builder()
                .code(locationCode) // 6 characters, valid
                .cityName("London") // 6 characters, valid
                .regionName("England")
                .countryName("United Kingdom")
                .countryCode("UK") // 2 characters, valid
                .enabled(true)
                .trashed(false)
                .build();
        String requestURI = API_PATH + "/" + locationCode;
        Mockito.when(locationService.findByLocationCode(locationCode)).thenReturn(location);
        DailyWeatherId id1=DailyWeatherId
                .builder()
                .month(5)
                .dayOfMonth(23)
                .locationCode(locationCode)
                .build();
        DailyWeatherId id2=DailyWeatherId
                .builder()
                .month(6)
                .dayOfMonth(12)
                .locationCode(locationCode)
                .build();
        DailyWeather dailyWeather1=DailyWeather
                .builder()
                .minTemp(21)
                .maxTemp(33)
                .precipitation(12)
                .status("Windy")
                .location(location)
                .dailyWeatherId(id1)
                .build();

        DailyWeather dailyWeather2=DailyWeather
                .builder()
                .minTemp(3)
                .maxTemp(20)
                .precipitation(33)
                .status("Rainy")
                .location(location)
                .dailyWeatherId(id2)
                .build();
        Mockito.when(dailyWeatherService.getByLocationCode(locationCode))
                .thenReturn(List.of(dailyWeather1, dailyWeather2));

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dailyWeatherId.locationCode", is("LDN_UK")))
                .andExpect(jsonPath("$[1].location.cityName",is("London")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String locationCode="LDN_UK";
        String requestURI = API_PATH + "/" + locationCode;
        List<DailyWeatherDTO> list=new ArrayList<>();
        mockMvc.perform(put(requestURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isBadRequest());


    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "LDN_UK";
        String requestURI = API_PATH + "/" + locationCode;

        // Mock location to be used inside the controller
        Location location = Location.builder()
                .code(locationCode)
                .cityName("London")
                .regionName("England")
                .countryName("United Kingdom")
                .countryCode("UK")
                .listDailyWeather(new ArrayList<>())
                .enabled(true)
                .trashed(false)
                .build();

        // Ensure the controller can resolve the location
        Mockito.when(locationService.findByLocationCode(locationCode)).thenReturn(location);

        // Incoming request DTOs
        DailyWeatherDTO dto1 = DailyWeatherDTO.builder()
                .dayOfMonth(12).month(8)
                .minTemp(22).maxTemp(34)
                .precipitation(12).status("Cloudy")
                .build();

        DailyWeatherDTO dto2 = DailyWeatherDTO.builder()
                .dayOfMonth(10).month(12)
                .minTemp(27).maxTemp(17)
                .precipitation(44).status("Rainy")
                .build();

        List<DailyWeatherDTO> dtoList = List.of(dto1, dto2);

        // What the service is expected to return
        DailyWeatherId id1 = new DailyWeatherId(12, 8, locationCode);
        DailyWeatherId id2 = new DailyWeatherId(10, 12, locationCode);

        DailyWeather entity1 = DailyWeather.builder()
                .dailyWeatherId(id1)
                .minTemp(22).maxTemp(34).precipitation(12).status("Cloudy")
                .location(location)
                .build();

        DailyWeather entity2 = DailyWeather.builder()
                .dailyWeatherId(id2)
                .minTemp(27).maxTemp(17).precipitation(44).status("Rainy")
                .location(location)
                .build();

        List<DailyWeather> entityList = List.of(entity1, entity2);

        // Mock the service
        Mockito.when(dailyWeatherService.updateByLocationCode(eq(locationCode), anyList()))
                .thenReturn(entityList);

        // Perform the request
        mockMvc.perform(put(requestURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andExpect(status().isOk())
                .andDo(print());
    }







}