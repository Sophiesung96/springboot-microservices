package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.RootEntity;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class MainController {

    @GetMapping("/")
    public ResponseEntity<RootEntity> handleBaseURI(){
        return ResponseEntity.ok(createRootEntity());
    }

    private RootEntity createRootEntity(){
        RootEntity rootEntity = new RootEntity();
        String locationUrl= linkTo(methodOn(LocationController.class)
                .listAllLocations()).toString();
        String locationByCodeUrl=linkTo(methodOn(LocationController.class)
                .getLocation(null))
                .toString();
        rootEntity.setLocationsUrl(locationUrl);
        rootEntity.setLocationByCodeUrl(locationByCodeUrl);

        String realTimeIpUrl=linkTo(methodOn(RealTimeWeatherController.class)
                .getRealTimeWeatherByIPAddress(null)).toString();

        rootEntity.setRealtimeWeatherByIpUrl(realTimeIpUrl);

        String realTimeCodeUrl=linkTo(methodOn(RealTimeWeatherController.class)
                .getRealTimeWeatherByLocationCode(null)).toString();
        rootEntity.setRealtimeWeatherByCodeUrl(realTimeCodeUrl);

        String hourlyIpUrl=linkTo(methodOn(HourlyWeatherApiController.class)
                .listHourlyForecastByIPAddress(null)).toString();

        rootEntity.setHourlyForecastByIpUrl(hourlyIpUrl);

        String hourlyCodeUrl=linkTo(methodOn(HourlyWeatherApiController.class)
                .listHourlyForecastByLocationCode(null,null)).toString();

        rootEntity.setHourlyForecastByCodeUrl(hourlyCodeUrl);

        String dailyIpUrl=linkTo(methodOn(DailyWeatherController.class)
                .listDailyWeatherByIpAddress(null)).toString();

        rootEntity.setDailyForecastByIpUrl(dailyIpUrl);

        String dailyCodeUrl=linkTo(methodOn(DailyWeatherController.class)
                .listDailyForecastByLocationCode(null)).toString();

        rootEntity.setDailyForecastByCodeUrl(dailyCodeUrl);

        String fullWeatherIpUrl=linkTo(methodOn(FullWeatherApiController.class)
                .getFullWeatherByIPAddress(null)).toString();

        rootEntity.setFullWeatherByIpUrl(fullWeatherIpUrl);

        String fullWeatherCodeUrl=linkTo(methodOn(FullWeatherApiController.class)
                .getFullWeatherByLocationCode(null)).toString();

        rootEntity.setFullWeatherByCodeUrl(fullWeatherCodeUrl);



        return rootEntity;
    }
}
