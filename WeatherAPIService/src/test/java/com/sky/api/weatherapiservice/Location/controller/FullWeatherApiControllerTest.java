package com.sky.api.weatherapiservice.Location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.*;
import com.sky.api.weatherapiservice.DTO.*;
import com.sky.api.weatherapiservice.config.SpyBeanConfig;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.FullWeatherService;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FullWeatherApiController.class)
@ActiveProfiles("test")
@Import({SpyBeanConfig.class, WeatherMapper.class, SecurityConfigForTestControllerTests.class})
class FullWeatherApiControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FullWeatherService fullWeatherService;
    @MockitoBean
    GeoLocationService geoLocationService;

    @MockitoBean
    LocationService locationService;

    @MockitoBean
    WeatherMapper weatherMapper;

    @Autowired
    MockMvc mockMvc;


    private static final String END_PATH="/v1/full";
    private static final String RESPONSE_CONTENT_TYPE = "application/hal+json";
    private static final String REQUEST_CONTENT_TYPE = "application/json";

    @MockitoBean
    private FullWeatherModelAssembler assembler;






    @Test
    public void testGetByIPShouldReturn400BadRequest() throws Exception {
        GeoLocationException ex=new GeoLocationException("Geolocation error");
        when(geoLocationService.getLocation(Mockito.anyString())).thenThrow(ex);

        mockMvc.perform(get(END_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testByGetIPShouldReturn404NotFound() throws Exception {
        Location location= Location.builder().code("DELHI_IN").build();
        when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
        LocationNotFoundException ex=new LocationNotFoundException(location.getCode());
        when(fullWeatherService.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]",is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn200OK() throws Exception {
        Location location=Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States Of America")
                .build();
        DailyWeatherId id1=DailyWeatherId
                .builder()
                .month(5)
                .dayOfMonth(23)
                .locationCode(location.getCode())
                .build();
        DailyWeatherId id2=DailyWeatherId
                .builder()
                .month(6)
                .dayOfMonth(12)
                .locationCode(location.getCode())
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
        location.setListDailyWeather(List.of(dailyWeather1,dailyWeather2));

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

        when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(fullWeatherService.getByLocation(location)).thenReturn(location);
        String expectedLocation=location.toString();
        mockMvc.perform(get(END_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location",is(expectedLocation)))
                .andExpect(jsonPath("$.daily_forecast.dailyWeatherDTOList[0].dayOfMonth", is(23)))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode="ABC123";
        String requestURI=END_PATH+"/"+locationCode;
        LocationNotFoundException ex=new LocationNotFoundException(locationCode);
        when(fullWeatherService.get(locationCode)).thenThrow(ex);
        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]",is(ex.getMessage())))
                .andDo(print());

    }

    @Test
    public void testGetByCodeShouldReturn200OK() throws Exception {
        String locationCode="NYC_USA";
        String requestURI=END_PATH+"/"+locationCode;
        Location location=Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States Of America")
                .build();
        DailyWeatherId id1=DailyWeatherId
                .builder()
                .month(5)
                .dayOfMonth(23)
                .locationCode(location.getCode())
                .build();
        DailyWeatherId id2=DailyWeatherId
                .builder()
                .month(6)
                .dayOfMonth(12)
                .locationCode(location.getCode())
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
        location.setListDailyWeather(List.of(dailyWeather1,dailyWeather2));

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

        when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(fullWeatherService.get(location.getCode())).thenReturn(location);
        mockMvc.perform(get(requestURI))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location",is(location.toString())))
                .andExpect(jsonPath("$.hourly_forecast.hourlyWeatherList[0].temperature",is(70)))
                .andDo(print());

    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoHourlyWeather() throws Exception {
        String locationCode="NYC_USA";
        String requestURI=END_PATH+"/"+locationCode;
        FullWeatherDTO dto=new FullWeatherDTO();


        mockMvc.perform(put(requestURI)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",is("Hourly Weather data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoDailyWeather() throws Exception {
        String locationCode="NYC_USA";
        String requestURI=END_PATH+"/"+locationCode;
        FullWeatherDTO dto=new FullWeatherDTO();
        Location location=Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States Of America")
                .build();
        HourlyWeatherId hourlyWeatherID1 = new HourlyWeatherId(13, "NYC_USA");

        HourlyWeather hourlyWeather1 = HourlyWeather.builder()
                .id(hourlyWeatherID1)
                .precipitation(10)
                .temperature(70)
                .location(location)
                .build();
        List<HourlyWeatherDTO> dtoList = List.of(hourlyWeather1).stream()
                .map(hw -> HourlyWeatherDTO.builder()
                        .temperature(hw.getTemperature())
                        .precipitation(hw.getPrecipitation())
                        .hourOfDay(hw.getId().getHourOfDay())
                        .status("Sunny")
                        .build())
                        .collect(Collectors.toList());

        HourlyWeatherListDTO hourlyWeatherListDTO = HourlyWeatherListDTO.builder()
                .hourlyWeatherList(dtoList)
                .build();


        mockMvc.perform(put(requestURI)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",is("Hourly Weather data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OKBecauseInvalidRealtimeWeather() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_PATH + "/" + locationCode;

        // Prepare the Location entity
        Location location = Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States Of America")
                .build();

        // Create and set hourly weather
        HourlyWeatherId hourlyWeatherId = new HourlyWeatherId(13, locationCode);
        HourlyWeather hourlyWeather = HourlyWeather.builder()
                .id(hourlyWeatherId)
                .temperature(70)
                .precipitation(10)
                .location(location)
                .build();
        location.setListHourlyWeather(List.of(hourlyWeather));

        // Create and set daily weather
        DailyWeatherId dailyWeatherId = DailyWeatherId.builder()
                .dayOfMonth(23)
                .month(5)
                .locationCode(locationCode)
                .build();
        DailyWeather dailyWeather = DailyWeather.builder()
                .dailyWeatherId(dailyWeatherId)
                .minTemp(21)
                .maxTemp(33)
                .precipitation(12)
                .status("Windy")
                .location(location)
                .build();
        location.setListDailyWeather(List.of(dailyWeather));

        // Set the realtime weather
        RealTimeWeather realtimeWeather = RealTimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .precipitation(88)
                .windSpeed(5)
                .status("Cloudy")
                .lastUpdated(new Date())
                .location(location)
                .build();
        location.setRealTimeWeather(realtimeWeather);

        // Create FullWeatherDTO (request body)
        FullWeatherDTO dto = new FullWeatherDTO();
        dto.setLocation(locationCode);
        dto.setRealtimeWeather(RealtimeWeatherDTO.builder()
                .temperature(12)
                .humidity(32)
                .precipitation(88)
                .windSpeed(5)
                .status("Cloudy")
                .lastUpdated(new Date())
                .build());

        dto.setHourlyWeather(HourlyWeatherListDTO.builder()
                        .location(locationCode)
                .hourlyWeatherList(List.of(HourlyWeatherDTO.builder()
                        .hourOfDay(13)
                        .temperature(70)
                        .precipitation(10)
                        .status("Sunny")
                        .build()))
                .build());

        dto.setDailyWeather(DailyWeatherListDTO.builder()
                        .location(locationCode)
                .dailyWeatherDTOList(List.of(DailyWeatherDTO.builder()
                        .dayOfMonth(23)
                        .month(5)
                        .minTemp(21)
                        .maxTemp(33)
                        .precipitation(12)
                        .status("Windy")
                        .build()))
                .build());

        // Mock mapper behavior
        Location mockLocation = Location.builder().code(locationCode).build();

        when(weatherMapper.fullDtoToLocationEntity(any(FullWeatherDTO.class)))
                .thenReturn(mockLocation);
        when(fullWeatherService.update(eq(locationCode), any(Location.class)))
                .thenReturn(mockLocation);
        when(weatherMapper.fullEntity2Dto(any(Location.class)))
                .thenReturn(dto);
        when(assembler.toModel(any(FullWeatherDTO.class)))
                .thenReturn(EntityModel.of(dto));



        // Perform the test request
        mockMvc.perform(put(requestURI)
                        .contentType(REQUEST_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
                .andExpect(jsonPath("$.hourly_forecast.hourly_weather_list[0].hour_of_day", is(13)))
                .andExpect(jsonPath("$.daily_forecast.daily_weather_dtolist[0].precipitation", is(12)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/full/" + locationCode)))
                .andDo(print());
    }


}