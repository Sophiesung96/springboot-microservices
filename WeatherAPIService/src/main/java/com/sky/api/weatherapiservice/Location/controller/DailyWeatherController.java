package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.*;
import com.sky.api.weatherapiservice.DTO.*;
import com.sky.api.weatherapiservice.Exception.DailyWeatherForecastException;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.DailyWeatherService;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.Utility.CommonUtility;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/daily")
@RequiredArgsConstructor
public class DailyWeatherController {

    private final DailyWeatherService dailyWeatherService;
    private final LocationService locationService;
    private final GeoLocationService  geoLocationService;

    private final WeatherMapper weatherMapper;


    @GetMapping()
    public ResponseEntity<?> listDailyWeatherByIpAddress(HttpServletRequest request){

        String ipAddress= CommonUtility.getIpAddress(request);
        Location locationFromIP=geoLocationService.getLocation(ipAddress);
        List<DailyWeather> dailyWeatherList=dailyWeatherService.getByLocationCode(locationFromIP.getCode());
        if(dailyWeatherList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(addLinksByIP(weatherMapper.listDailyEntity2DTO(dailyWeatherList)));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable String locationCode){
        if(locationCode==null || locationCode.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        Location location=locationService.findByLocationCode(locationCode);
        if(location==null){
            throw new LocationNotFoundException(locationCode);
        }
        List<DailyWeather> dailyWeatherList=dailyWeatherService.getByLocationCode(location.getCode());
        if(dailyWeatherList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var entity=weatherMapper.listDailyEntity2DTO(dailyWeatherList);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(6, TimeUnit.HOURS)
                .cachePublic()).body(entity);
    }






    //helper method
    private List<DailyWeather> listDTO2ListEntity(List<DailyWeatherDTO> dtoList, String locationCode) {
        Location location = locationService.findByLocationCode(locationCode);

        if (location == null) {
            throw new LocationNotFoundException(locationCode);
        }

        return dtoList.stream()
                .map(dto -> weatherMapper.mapDTO2Entity(dto, location))
                .collect(Collectors.toList());
    }


    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateDailyWeather(@PathVariable String locationCode,
                                                @Valid @RequestBody List<DailyWeatherDTO> dailyWeatherDTOS) throws BadRequestException {
        if (dailyWeatherDTOS.isEmpty()) {
            throw new DailyWeatherForecastException("Daily forecast data cannot be empty");
        }
        List<DailyWeather> dailyWeatherList=listDTO2ListEntity(dailyWeatherDTOS,locationCode);
        List<DailyWeather> updatedList=dailyWeatherService.updateByLocationCode(locationCode,
                dailyWeatherList);

        return ResponseEntity.ok().body(weatherMapper.listDailyEntity2DTO(updatedList));
    }

    private EntityModel<DailyWeatherListDTO> addLinksByIP(DailyWeatherListDTO dailyWeatherListDTO){

        return EntityModel.of(dailyWeatherListDTO)
                .add(
                    linkTo(methodOn(DailyWeatherController.class)
                            .listDailyWeatherByIpAddress(null))
                            .withSelfRel())
                .add(
                    linkTo(methodOn(RealTimeWeatherController.class)
                            .getRealTimeWeatherByIPAddress(null))
                            .withRel("realtime_weather"))
                .add(
                    linkTo(methodOn(DailyWeatherController.class)
                            .listDailyWeatherByIpAddress(null))
                            .withRel("daily_forecast"))

                .add(
                    linkTo(methodOn(FullWeatherApiController.class)
                            .getFullWeatherByIPAddress(null))
                            .withRel("full_forecast"));

    }

    private EntityModel<DailyWeatherListDTO> addLinksByCode(DailyWeatherListDTO dailyWeatherListDTO, String locationCode){

        return EntityModel.of(dailyWeatherListDTO)
                .add(
                    linkTo(methodOn(DailyWeatherController.class)
                            .listDailyForecastByLocationCode(null))
                            .withSelfRel())
                .add(
                linkTo(methodOn(RealTimeWeatherController.class)
                        .getRealTimeWeatherByIPAddress(null))
                        .withRel("realtime_weather"))
                .add(
                linkTo(methodOn(DailyWeatherController.class)
                        .listDailyWeatherByIpAddress(null))
                        .withRel("daily_forecast"))

                .add(
                    linkTo(methodOn(FullWeatherApiController.class)
                            .getFullWeatherByIPAddress(null))
                            .withRel("full_forecast"));
    }




}
