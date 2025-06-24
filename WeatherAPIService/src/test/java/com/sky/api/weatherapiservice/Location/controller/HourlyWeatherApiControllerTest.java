package com.sky.api.weatherapiservice.Location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.HourlyWeatherId;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.DTO.HourlyWeatherDTO;
import com.sky.api.weatherapiservice.DTO.HourlyWeatherListDTO;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.HourlyWeatherService;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.Location.service.RealTimeWeatherService;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(HourlyWeatherApiController.class)
@AutoConfigureMockMvc
public class HourlyWeatherApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    HourlyWeatherService hourlyWeatherService;

    @MockitoBean
    GeoLocationService geoLocationService;

    @MockitoBean
    LocationService locationService;

    @MockitoBean
    RealTimeWeatherService realTimeWeatherService;

    @MockitoBean
    WeatherMapper weatherMapper;

    private static final String END_POINT_PATH="/v1/hourly";
    private static final String RESPONSE_CONTENT_TYPE = "application/hal+json";
    private static final String X_CURRENT_HOUR = "X-Current-Hour";

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn200OK() throws Exception {
        int currentHour = 9;

        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        RealTimeWeather realTimeWeather = RealTimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .lastUpdated(new Date())
                .precipitation(12)
                .status("Cloudy")
                .windSpeed(5)
                .location(location)
                .build();

        location.setRealTimeWeather(realTimeWeather);
        String locationCode=location.getCode();

        HourlyWeatherId hourlyWeatherID1 = new HourlyWeatherId(13, "NYC_USA");
        HourlyWeatherId hourlyWeatherID2 = new HourlyWeatherId(14, "NYC_USA");

        HourlyWeather hourlyWeather1 = HourlyWeather.builder()
                .id(hourlyWeatherID1)
                .precipitation(10)
                .temperature(70)
                .location(location)
                .build();

        HourlyWeather hourlyWeather2 = HourlyWeather.builder()
                .id(hourlyWeatherID2)
                .precipitation(18)
                .temperature(77)
                .location(location)
                .build();

        location.setListHourlyWeather(List.of(hourlyWeather1,hourlyWeather2));

        when(locationService.findByCountryCodeCityName(location.getCountryCode(), location.getCityName()))
                .thenReturn(location);

        when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(hourlyWeather1, hourlyWeather2));
        HourlyWeatherListDTO dto = new HourlyWeatherListDTO();
        dto.setLocation(location.toString());
        dto.setHourlyWeatherList(List.of(
                new HourlyWeatherDTO(10, 70, 10,"Sunny"),
                new HourlyWeatherDTO(11, 77, 18,"Rainy")
        ));
        HourlyWeatherListDTO dtoList=weatherMapper.listHourlyEntity2DTO(List.of(hourlyWeather1, hourlyWeather2));
        when(weatherMapper.listHourlyEntity2DTO(List.of(hourlyWeather1, hourlyWeather2)))
                .thenReturn(dto);


        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_weather_list[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/hourly/" + locationCode)))
                .andExpect(jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime/" + locationCode)))
                .andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + locationCode)))
                .andDo(print());
    }


    @Test
    public void testGetByIPShouldReturn204NoContent() throws Exception {

        Location location= Location.builder().code("DELHL_IN").build();
        when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location,9)).thenReturn(new ArrayList<>());
        mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour", "9"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn204BadRequest() throws Exception {
        String locationCode="test";
        when(hourlyWeatherService.getByLocationCode(locationCode,2)).thenReturn(null);
        String url=END_POINT_PATH+"/"+locationCode;
        mockMvc.perform(get(url).header("X-Current-Hour",9))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception {
        String locationCode="test";
        List<HourlyWeatherDTO> hourlyWeatherList= Collections.emptyList();
        String url=END_POINT_PATH+"/"+locationCode;
        String mappedObject=objectMapper.writeValueAsString(hourlyWeatherList);
        mockMvc.perform(put(url).content(mappedObject).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",is("Hourly forecast data cannot be empty!")))
                .andDo(print());

    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode="NYC_USA";
        String requestURI=END_POINT_PATH+"/"+locationCode;
        HourlyWeatherDTO weatherDTO=HourlyWeatherDTO.builder()
                .precipitation(120)
                .hourOfDay(10)
                .temperature(200)
                .status("Cloudy").build();
        List<HourlyWeatherDTO> list=List.of(weatherDTO);
        String mappedObject=objectMapper.writeValueAsString(list);
        mockMvc.perform(put(requestURI).contentType("application/json").content(mappedObject))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        // Define variables and request setup
        String locationCode = "LDN_UK";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        // Create mock HourlyWeatherDTOs
        HourlyWeatherDTO weatherDTO1 = HourlyWeatherDTO.builder()
                .precipitation(10)
                .hourOfDay(13)
                .temperature(70)
                .status("Cloudy")
                .build();

        HourlyWeatherDTO weatherDTO2 = HourlyWeatherDTO.builder()
                .precipitation(18)
                .hourOfDay(14)
                .temperature(77)
                .status("Rainy")
                .build();

        List<HourlyWeatherDTO> dtoList = List.of(weatherDTO1, weatherDTO2);

        // Create mock Location
        Location location = Location.builder()
                .code("LDN_UK")
                .cityName("London")
                .regionName("England")
                .countryCode("UK")
                .countryName("United Kingdom")
                .build();

        // Create mock HourlyWeather entities
        HourlyWeatherId hourlyWeatherID1 = new HourlyWeatherId(13, "LDN_UK");
        HourlyWeatherId hourlyWeatherID2 = new HourlyWeatherId(14, "LDN_UK");

        HourlyWeather hourlyWeather1 = HourlyWeather.builder()
                .id(hourlyWeatherID1)
                .precipitation(10)
                .temperature(70)
                .location(location)
                .build();

        HourlyWeather hourlyWeather2 = HourlyWeather.builder()
                .id(hourlyWeatherID2)
                .precipitation(18)
                .temperature(77)
                .location(location)
                .build();

        List<HourlyWeather> hourlyWeatherList = List.of(hourlyWeather1, hourlyWeather2);

        when(locationService.findByLocationCode(locationCode)).thenReturn(location);
        // Mock behavior
        when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode),Mockito.anyList())).thenReturn(hourlyWeatherList);

        // Serialize request body
        String requestBody = objectMapper.writeValueAsString(dtoList);

        // Perform PUT request and validate response
        mockMvc.perform(put(requestURI)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id.hourOfDay",is(13)))
                .andExpect(jsonPath("$[0].id.locationCode",is("LDN_UK")))
                .andExpect(jsonPath("$[0].precipitation", is(10)))
                .andExpect(jsonPath("$[0].temperature", is(70)))
                .andExpect(jsonPath("$[1].id.hourOfDay", is(14)))
                .andExpect(jsonPath("$[1].id.locationCode", is("LDN_UK")))
                .andExpect(jsonPath("$[1].precipitation", is(18)))
                .andExpect(jsonPath("$[1].temperature", is(77)))
                .andDo(print());
    }




}