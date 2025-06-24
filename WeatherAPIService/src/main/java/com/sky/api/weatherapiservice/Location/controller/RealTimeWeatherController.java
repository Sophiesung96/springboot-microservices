package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.DTO.RealtimeWeatherDTO;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.RealTimeWeatherService;
import com.sky.api.weatherapiservice.Utility.CommonUtility;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/realtime")
@Slf4j
public class RealTimeWeatherController {

    private final WeatherMapper weatherMapper;
    private RealTimeWeatherService realTimeWeatherService;
    private GeoLocationService geoLocationService;


    public RealTimeWeatherController(@Autowired RealTimeWeatherService realTimeWeatherService,
                                     @Autowired GeoLocationService geoLocationService, WeatherMapper weatherMapper) {
        this.realTimeWeatherService = realTimeWeatherService;
        this.geoLocationService = geoLocationService;
        this.weatherMapper = weatherMapper;
    }

    @GetMapping
    public ResponseEntity<?> getRealTimeWeatherByIPAddress(HttpServletRequest request){
       String ipAddress= CommonUtility.getIpAddress(request);
       Location locationFromIP=geoLocationService.getLocation(ipAddress);
       RealTimeWeather realTimeWeather=realTimeWeatherService.getByLocation(locationFromIP);
        RealtimeWeatherDTO dto=weatherMapper.mapRealEntiyToDTO(realTimeWeather);
       return ResponseEntity.ok(dto);
    }


    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealTimeWeatherByLocationCode(@PathVariable String locationCode){

        RealTimeWeather realTimeWeather=realTimeWeatherService.getByLocationCode(locationCode);
        RealtimeWeatherDTO dto=weatherMapper.mapRealEntiyToDTO(realTimeWeather);
        return ResponseEntity.ok(addLinksByIP(dto));

    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealTimeWeather(@PathVariable String locationCode,@RequestBody @Valid RealTimeWeather realTimeWeather){

        RealTimeWeather updatedRealtimeWeather=realTimeWeatherService.update(locationCode,realTimeWeather);
        return ResponseEntity.ok(updatedRealtimeWeather);

    }

    private RealtimeWeatherDTO addLinksByIP(RealtimeWeatherDTO realtimeWeatherDTO){
        realtimeWeatherDTO.add(
                linkTo(methodOn(RealTimeWeatherController.class)
                .getRealTimeWeatherByIPAddress(null))
                        .withSelfRel());
        realtimeWeatherDTO.add(
                linkTo(methodOn(HourlyWeatherApiController.class)
                        .listHourlyForecastByIPAddress(null))
                        .withRel("hourly_forecast"));

        realtimeWeatherDTO.add(
                linkTo(methodOn(DailyWeatherController.class)
                        .listDailyWeatherByIpAddress(null))
                        .withRel("daily_forecast"));
        return realtimeWeatherDTO;
    }

}
