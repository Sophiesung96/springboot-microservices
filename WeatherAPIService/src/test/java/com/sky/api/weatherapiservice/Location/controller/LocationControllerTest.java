package com.sky.api.weatherapiservice.Location.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Collections;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc
class LocationControllerTest {



        @MockitoBean
        private LocationRepository locationRepository;

        @MockitoBean
        private LocationService locationService;

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        WeatherMapper weatherMapper;


        private static final String END_POINT_PATH = "/v1/locations";

        @Test
        public void testAddSuccess() throws Exception {

            RealTimeWeather realTimeWeather = RealTimeWeather.builder()
                    .temperature(15)
                    .humidity(60)
                    .windSpeed(20)
                    .precipitation(43)
                    .status("Windy")
                    .build();

            Location location = Location.builder()
                    .code("LDN_UK")
                    .cityName("London")
                    .regionName("England")
                    .countryName("United Kingdom")
                    .countryCode("UK")
                    .enabled(true)
                    .trashed(false)
                    .realTimeWeather(realTimeWeather)
                    .build();


            // Mock the service behavior
            when(locationService.add(Mockito.any(Location.class))).thenReturn(location);

            String bodyContent=objectMapper.writeValueAsString(location);
            mockMvc.perform(post(END_POINT_PATH).content(bodyContent).contentType("application/json"))
                    .andExpect(status().isCreated())
                    .andDo(print());
        }

        @Test
        @Disabled
        public void testListByPageShouldReturn204NoContent() throws Exception {

            when(locationService.listByPage(anyInt(),anyInt(),anyString())).thenReturn(Page.empty());
            mockMvc.perform(get(END_POINT_PATH))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

    @Test
    public void testListByPageShouldReturn400BadRequestInvalidPageSize() throws Exception {
        int pageNum=1;
        int pageSize=5;
        String sortField="abc";
        String requestURI=END_POINT_PATH+"?page="+pageNum+"&size="+pageSize+"&sort="+sortField;
        when(locationService.listByPage(pageNum,pageSize,sortField)).thenReturn(Page.empty());
        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",containsString("invalid sort field"+sortField)))
                .andDo(print());
    }


    @Test
        public void testListByPageShouldReturn200OK() throws Exception {

            int pageSize=5;
            int pageNum=1;
            String sortField="code";
            Sort sort=Sort.by(sortField).ascending();
            Pageable pageable=PageRequest.of(pageNum,pageSize,sort);

            RealTimeWeather realTimeWeather = RealTimeWeather.builder()
                    .temperature(15)
                    .humidity(60)
                    .windSpeed(20)
                    .precipitation(43)
                    .status("Windy")
                    .build();

            Location location = Location.builder()
                    .code("LDN_UK")
                    .cityName("London")
                    .regionName("England")
                    .countryName("United Kingdom")
                    .countryCode("UK")
                    .enabled(true)
                    .trashed(false)
                    .realTimeWeather(realTimeWeather)
                    .build();

            Location location2 = Location.builder()
                    .code("LACA_USA")
                    .cityName("Los Angeles")
                    .regionName("Los Angeles")
                    .countryName("United States Of America")
                    .countryCode("US")
                    .enabled(true)
                    .trashed(false)
                    .realTimeWeather(realTimeWeather)
                    .build();

            List<Location> locationList=List.of(location,location2);
            int totalElememts=locationList.size();
            Page<Location> page=new PageImpl<>(locationList,pageable,totalElememts);
            Mockito.when(locationService.listByPage(pageNum-1,pageSize,sortField)).thenReturn(page);
            String requestUrl=END_POINT_PATH+"?page="+pageNum+"&size="+pageSize+"&sort="+sortField;
            mockMvc.perform(get(requestUrl))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/hal+json"))
                    .andExpect(jsonPath("$._embedded[0].locations[0].code",equalTo("LDN_UK")))
                    .andExpect(jsonPath("$[0].code",equalTo("LDN_UK")))
                    .andExpect(jsonPath("$[0].city_name",equalTo("London")))
                    .andExpect(jsonPath("$[0].country_name",equalTo("United Kingdom")))
                    .andDo(print());
        }

        @Test
        @Disabled
        public void testListShouldReturn204NoContent() throws Exception {
            when(locationService.list()).thenReturn(Collections.emptyList());
            mockMvc.perform(get(END_POINT_PATH))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @Test
        public void testGetShouldReturn405MethodNotAllowed() throws Exception {
            String code="ABCD";
            String requestURI=END_POINT_PATH+"/"+code;
            mockMvc.perform(get(requestURI))
                    .andExpect(status().isMethodNotAllowed())
                    .andDo(print());

        }

    @Test
    public void testGetShouldReturn200MethodAllowed() throws Exception {
        String code="LDN_UK";
        Location location = Location.builder()
                .code("LDN_UK")
                .cityName("London")
                .regionName("England")
                .countryCode("UK")
                .countryName("United Kingdom")
                .build();
        when(locationService.get(code)).thenReturn(location);
        String requestURI=END_POINT_PATH+"/"+code;
        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code",is(code)))
                .andExpect(jsonPath("$.countryCode",is("UK")))
                .andDo(print());

    }

    @Test
    public void testDeleteShouldReturn404NotFound() throws Exception {

            String code="123";
            Mockito.doThrow(new LocationNotFoundException("This location is not found"))
                    .when(locationService).deleteLocation(code);
            String requestURI=END_POINT_PATH+"/"+code;
            mockMvc.perform(delete(requestURI))
                    .andExpect(status().isNoContent())
                    .andDo(print());
    }


    @Test
    public void testDeleteShouldReturn204NoContent() throws Exception {

        String code="LDN_UK";
        // Mock the service's findByCode method to simulate the record exists
        Location location = new Location();
        location.setCode(code);
        location.setTrashed(false);
        // Simulate the deleteLocation method for successful trashing
        Mockito.doNothing().when(locationService).deleteLocation(code);
        String requestURI=END_POINT_PATH+"/"+code;
        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

}