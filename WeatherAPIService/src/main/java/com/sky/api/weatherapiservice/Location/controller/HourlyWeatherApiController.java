package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.HourlyWeatherId;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.DTO.HourlyWeatherDTO;
import com.sky.api.weatherapiservice.DTO.HourlyWeatherListDTO;
import com.sky.api.weatherapiservice.Exception.HourlyWeatherForecastException;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.HourlyWeatherService;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.Location.service.RealTimeWeatherService;
import com.sky.api.weatherapiservice.Utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/hourly")
@Slf4j
public class HourlyWeatherApiController {

    private GeoLocationService geoLocationService;
    private  HourlyWeatherService hourlyWeatherService;
    private LocationService locationService;
    private RealTimeWeatherService realTimeWeatherService;

    @Autowired
    public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeoLocationService geoLocationService
            , LocationService locationService, RealTimeWeatherService realTimeWeatherService) {
        this.geoLocationService = geoLocationService;
        this.hourlyWeatherService = hourlyWeatherService;
        this.locationService = locationService;
        this.realTimeWeatherService=realTimeWeatherService;
    }


    @GetMapping
    public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
        String ipAddress= CommonUtility.getIpAddress(request);
        Location locationByIP=geoLocationService.getLocation(ipAddress);
        Location locationDB=locationService.findByCountryCodeCityName(locationByIP.getCountryCode(),locationByIP.getCityName());
        int currentHour= Integer.parseInt(request.getHeader("X-Current-Hour"));
        List<HourlyWeather> hourlyWeatherList=hourlyWeatherService.getByLocation(locationDB,currentHour);
        log.info("Hourly Weather List:{}", hourlyWeatherList.toString());
        if(hourlyWeatherList.isEmpty())
        {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listEntity2DTO(hourlyWeatherList));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable String locationCode,HttpServletRequest request){
        int currentHour= Integer.parseInt(request.getHeader("X-Current-Hour"));
        Optional<List<HourlyWeather>> hourlyWeatherList=hourlyWeatherService.getByLocationCode(locationCode,currentHour);
        if(!hourlyWeatherList.isEmpty())
        {
            log.warn("No weather data found for location: {} at hour: {}", locationCode, currentHour);
            return ResponseEntity.ok(listEntity2DTO(hourlyWeatherList.get()));
        }
         return ResponseEntity.noContent().build();
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable String locationCode,@RequestBody List<HourlyWeatherDTO> hourlyWeatherDTOList){

        HourlyWeatherListDTO hListDTO=mapDTOListToHourlyWeatherDTOList(hourlyWeatherDTOList,locationCode);
        List<HourlyWeather> hourlyWeatherList=listDTO2Entity(hListDTO);

        List<HourlyWeather> updatedHourlyWeather=hourlyWeatherService.updateByLocationCode(locationCode,hourlyWeatherList);

        if(updatedHourlyWeather.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedHourlyWeather);
    }



    private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> entity) {
        // Extract the location code from the first entity
        String location = entity.get(0).getLocation().toString();

        // Map the list of entities to DTOs
        List<HourlyWeatherDTO> hourlyWeatherDTOList = entity.stream()
                .map(this::mapEntity2DTO) // Use the mapper method for each item
                .collect(Collectors.toList());

        // Build and return the DTO
        return HourlyWeatherListDTO.builder()
                .location(location)
                .hourlyWeatherList(hourlyWeatherDTOList)
                .build();
    }


    private HourlyWeatherDTO mapEntity2DTO(HourlyWeather entity) {
        return HourlyWeatherDTO.builder()
                .precipitation(entity.getPrecipitation()) // Ensure correct field mapping
                .status(entity.getLocation().getRealTimeWeather().getStatus()) // Get status from RealTimeWeather
                .hourOfDay(entity.getId().getHourOfDay()) // Map hourOfDay
                .temperature(entity.getTemperature()) // Map temperature
                .build();
    }


    private List<HourlyWeather> listDTO2Entity(HourlyWeatherListDTO hourlyWeatherDTOList) {
        // Validate input
        if (hourlyWeatherDTOList == null || hourlyWeatherDTOList.getHourlyWeatherList() == null || hourlyWeatherDTOList.getHourlyWeatherList().isEmpty()) {
            throw new IllegalArgumentException("HourlyWeatherDTO list cannot be null or empty");
        }
        // Extract the location code from the DTO
        String locationCode = hourlyWeatherDTOList.getLocation();

        // Fetch the location from the service
        Location location = locationService.findByLocationCode(locationCode);

        // Map each DTO to an entity
        return hourlyWeatherDTOList.getHourlyWeatherList().stream() // Extract the internal list
                .map(dto -> mapDTO2Entity(dto, location)) // Use helper method for mapping
                .collect(Collectors.toList());
    }



    private HourlyWeather mapDTO2Entity(HourlyWeatherDTO hourlyWeatherDTO, Location location) {

        HourlyWeatherId hourlyWeatherId=new HourlyWeatherId(hourlyWeatherDTO.getHourOfDay(),location.getCode());
        return HourlyWeather.builder()
                .id(hourlyWeatherId)
                .temperature(hourlyWeatherDTO.getTemperature()) // Map temperature
                .precipitation(hourlyWeatherDTO.getPrecipitation()) // Map precipitation
                .location(location) // Use the fetched Location object
                .build();
    }

    private HourlyWeatherListDTO mapDTOListToHourlyWeatherDTOList(List<HourlyWeatherDTO> hourlyWeatherDTOList,String locationCode) {
        // Validate input
        if (hourlyWeatherDTOList == null || hourlyWeatherDTOList.isEmpty()) {
            throw new IllegalArgumentException("HourlyWeatherDTO list cannot be null or empty");
        }

        // Build and return HourlyWeatherDTOList
        return HourlyWeatherListDTO.builder()
                .location(locationCode) // Set location
                .hourlyWeatherList(hourlyWeatherDTOList) // Set the list of DTOs
                .build();
    }





}
