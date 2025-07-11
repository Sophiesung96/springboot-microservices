package com.sky.api.weatherapiservice.Location.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.DTO.LocationDTO;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityConfigForTestControllerTests.class)
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

        private static final String REQUEST_CONTENT_TYPE="application/json";
        private static final String RESPONSE_CONTENT_TYPE="application/hal+json";
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
            LocationDTO dto=LocationDTO.builder()
                    .code("LDN_UK")
                    .cityName("London")
                    .regionName("England")
                    .countryName("United Kingdom")
                    .countryCode("UK")
                    .build();
            when(weatherMapper.mapEntity2DTO(location)).thenReturn(dto);

            String bodyContent=objectMapper.writeValueAsString(location);
            mockMvc.perform(post(END_POINT_PATH).content(bodyContent).contentType("application/json"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$._links.self.href", Matchers.endsWith("/locations/LDN_UK")))
                    .andDo(print());
        }



    @Test
    public void testListByPageShouldReturn400BadRequestInvalidPageSize() throws Exception {
        int pageNum=1;
        int pageSize=5;
        String sortField="abc";
        Sort sort=Sort.by(sortField).ascending();
        String requestURI=END_POINT_PATH+"?page="+pageNum+"&size="+pageSize+"&sort="+sortField;
        when(locationService.listByPage(anyInt(),anyInt(),eq(sort),anyMap())).thenReturn(Page.empty());
        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",containsString("Invalid sort field: "+sortField)))
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn201Created() throws Exception {
        String code = "NYC_USA";
        Location location = new Location();
        location.setCode(code);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        LocationDTO dto = new LocationDTO();
        dto.setCode(location.getCode());
        dto.setCityName(location.getCityName());
        dto.setRegionName(location.getRegionName());
        dto.setCountryCode(location.getCountryCode());
        dto.setCountryName(location.getCountryName());
        dto.setEnabled(location.isEnabled());

        Mockito.when(weatherMapper.mapDTO2Entity(dto)).thenReturn(location);
        Mockito.when(locationService.add(location)).thenReturn(location);
        Mockito.when(weatherMapper.mapEntity2DTO(location)).thenReturn(dto);

        String bodyContent = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post(END_POINT_PATH).contentType(REQUEST_CONTENT_TYPE).content(bodyContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.code", is(code)))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andExpect(header().string("Location", "v1/locations/" + code))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost" + END_POINT_PATH + "/" + code)))
                .andExpect(jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime/" + code)))
                .andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + code)))
                .andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + code)))
                .andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + code)))
                .andDo(print());
    }
    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        LocationDTO dto = new LocationDTO();
        dto.setCode("ABCDEF");
        dto.setCityName("Los Angeles");
        dto.setRegionName("California");
        dto.setCountryCode("US");
        dto.setCountryName("United States of America");
        dto.setEnabled(true);

        Location location=Location.builder()
                .code(dto.getCode())
                .regionName(dto.getRegionName())
                .countryName(dto.getCountryName())
                .cityName(dto.getCityName())
                .enabled(dto.isEnabled())
                .build();

        LocationNotFoundException ex = new LocationNotFoundException(location.getCityName());

        Mockito.when(locationService.update(Mockito.any())).thenThrow(ex);

        when(weatherMapper.mapEntity2DTO(location)).thenReturn(dto);

        String bodyContent = objectMapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType(REQUEST_CONTENT_TYPE).content(bodyContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception {
        LocationDTO location = new LocationDTO();
        location.setCityName("Los Angeles");
        location.setRegionName("California");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = objectMapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType(REQUEST_CONTENT_TYPE).content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        String code = "NYC_USA";
        Location location = new Location();
        location.setCode(code);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");}



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

            List<LocationDTO> dtoList = locationList.stream()
                    .map(loc -> LocationDTO.builder()
                            .code(loc.getCode())
                            .cityName(loc.getCityName())
                            .regionName(loc.getRegionName())
                            .countryName(loc.getCountryName())
                            .countryCode(loc.getCountryCode())
                            .enabled(loc.isEnabled())
                            .build())
                    .toList();

            int totalElements=locationList.size();
            Page<Location> page=new PageImpl<>(locationList,pageable,totalElements);
            when(locationService.listByPage(anyInt(),anyInt(),eq(sort),anyMap())).thenReturn(page);
            String requestUrl=END_POINT_PATH+"?page="+pageNum+"&size="+pageSize+"&sort="+sortField;
            when(weatherMapper.listEntity2ListDTO(locationList)).thenReturn(dtoList);
            mockMvc.perform(get(requestUrl))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/hal+json"))
                    .andExpect(header().string("Cache-Control",containsString("max-age=1800")))
                    .andExpect(jsonPath("$._embedded.locations[0].code", is("LDN_UK")))
                    .andExpect(jsonPath("$._embedded.locations[0].city_name", equalTo("London")))
                    .andExpect(jsonPath("$._embedded.locations[0].country_name", equalTo("United Kingdom")))
                    .andExpect(jsonPath("$.page.size", equalTo(5)))
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
        public void testPaginationLinkOnlyOnePage() throws Exception{
            Location location = Location.builder()
                    .code("LDN_UK")
                    .cityName("London")
                    .regionName("England")
                    .countryName("United Kingdom")
                    .countryCode("UK")
                    .enabled(true)
                    .trashed(false)
                    .build();

            Location location2 = Location.builder()
                    .code("LACA_USA")
                    .cityName("Los Angeles")
                    .regionName("Los Angeles")
                    .countryName("United States Of America")
                    .countryCode("US")
                    .enabled(true)
                    .trashed(false)
                    .build();
            List<Location> locationList=List.of(location,location2);
            int pageSize=5;
            int pageNum=1;
            String sortField="code";
            Sort sort=Sort.by(sortField).ascending();
            List<Location> listLocations=new ArrayList<>(pageSize);
            for(int i=1; i<=pageSize; i++)
            {
                listLocations.add(new Location("CODE_"+i, "City "+i, "Region Name","US","Country Name"));

            }
            int totalElements=locationList.size();
            Pageable pageable=PageRequest.of(pageNum-1,pageSize,sort);
            Page page=new PageImpl(locationList,pageable,totalElements);
            when(locationService.listByPage(anyInt(),anyInt(),sort,anyMap())).thenReturn(page);
            List<LocationDTO> dtoList = locationList.stream()
                    .map(loc -> LocationDTO.builder()
                            .code(loc.getCode())
                            .cityName(loc.getCityName())
                            .regionName(loc.getRegionName())
                            .countryName(loc.getCountryName())
                            .countryCode(loc.getCountryCode())
                            .enabled(loc.isEnabled())
                            .build())
                    .toList();
            when(weatherMapper.listEntity2ListDTO(locationList)).thenReturn(dtoList);
            String hostName="http://localhost";
            int totalPage=totalElements/pageSize+1;
            String requestUrl=END_POINT_PATH+"?page="+pageNum+"&size="+pageSize+"&sort="+sortField;
            String firstPageURI=END_POINT_PATH+"?page="+1+"&size="+pageSize+"&sort="+sortField;
            String nextPageURI=END_POINT_PATH+"?page="+(pageNum+1)+"&size="+pageSize+"&sort="+sortField;
            String lastPageURI=END_POINT_PATH+"?page="+(totalPage)+"&size="+pageSize+"&sort="+sortField;
            String prevPageURI=END_POINT_PATH+"?page="+(pageNum-1)+"&size="+pageSize+"&sort="+sortField;
            mockMvc.perform(get(requestUrl))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/hal+json"))
                    .andExpect(jsonPath("$._links.self.href", containsString(hostName + requestUrl)))
                    .andExpect(jsonPath("$._links.first").doesNotExist())
                    .andExpect(jsonPath("$._links.next").doesNotExist())
                    .andExpect(jsonPath("$._links.prev").doesNotExist())
                    .andExpect(jsonPath("$._links.last").doesNotExist())
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