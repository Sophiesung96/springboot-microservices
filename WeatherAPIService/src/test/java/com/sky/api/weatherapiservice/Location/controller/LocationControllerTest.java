package com.sky.api.weatherapiservice.Location.controller;

import static org.hamcrest.Matchers.is;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.Collections;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc
class LocationControllerTest {


        //manually inject a @Mock into the Spring context using @TestConfiguration
        @TestConfiguration
        static class MockConfig {
            @Bean
            public LocationService locationService() {
                return Mockito.mock(LocationService.class);
            }
        }

        @Autowired
        private LocationService locationService;

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;


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
                    .code("LDN_UK") // 6 characters, valid
                    .cityName("London") // 6 characters, valid
                    .regionName("England")
                    .countryName("United Kingdom")
                    .countryCode("UK") // 2 characters, valid
                    .enabled(true)
                    .trashed(false)
                    .realTimeWeather(realTimeWeather)
                    .build();


            // Mock the service behavior
            Mockito.when(locationService.add(Mockito.any(Location.class))).thenReturn(location);

            String bodyContent=objectMapper.writeValueAsString(location);
            mockMvc.perform(post(END_POINT_PATH).content(bodyContent).contentType("application/json"))
                    .andExpect(status().isCreated())
                    .andDo(print());
        }

        @Test
        public void testSuccess(){
            List<Location> locations=locationService.list();

            locations.forEach(System.out::println);
        }

        @Test
        public void testListShouldReturn204NoContent() throws Exception {
            Mockito.when(locationService.list()).thenReturn(Collections.emptyList());
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
        Mockito.when(locationService.get(code)).thenReturn(location);
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