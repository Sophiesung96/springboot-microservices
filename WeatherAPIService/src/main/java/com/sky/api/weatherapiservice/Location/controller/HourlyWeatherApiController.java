package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.DTO.HourlyWeatherDTO;
import com.sky.api.weatherapiservice.DTO.HourlyWeatherListDTO;
import com.sky.api.weatherapiservice.DTO.RealtimeWeatherDTO;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.HourlyWeatherService;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.Location.service.RealTimeWeatherService;
import com.sky.api.weatherapiservice.Utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/hourly")
@Slf4j
@RequiredArgsConstructor
public class HourlyWeatherApiController {

    private final GeoLocationService geoLocationService;
    private  final HourlyWeatherService hourlyWeatherService;
    private final LocationService locationService;
    private final RealTimeWeatherService realTimeWeatherService;
    private final WeatherMapper weatherMapper;


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
        var entity=addLinksByLocation(weatherMapper.listHourlyEntity2DTO(hourlyWeatherList),locationDB.getCode());
        return ResponseEntity
                .ok().cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES).cachePublic())
                .body(entity);
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable String locationCode,HttpServletRequest request){
        int currentHour= Integer.parseInt(request.getHeader("X-Current-Hour"));
        Optional<List<HourlyWeather>> hourlyWeatherList=hourlyWeatherService.getByLocationCode(locationCode,currentHour);
        if(!hourlyWeatherList.isEmpty())
        {
            log.warn("No weather data found for location: {} at hour: {}", locationCode, currentHour);
            var entity=weatherMapper.listHourlyEntity2DTO(hourlyWeatherList.get());
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60,TimeUnit.MINUTES).cachePublic()
                    ).body(entity);
        }
         return ResponseEntity.noContent().build();
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable String locationCode,@RequestBody List<HourlyWeatherDTO> hourlyWeatherDTOList){

        HourlyWeatherListDTO hListDTO=weatherMapper.mapDTOListToHourlyWeatherDTOList(hourlyWeatherDTOList,locationCode);
        List<HourlyWeather> hourlyWeatherList=weatherMapper.listHourlyDTO2Entity(hListDTO);

        List<HourlyWeather> updatedHourlyWeather=hourlyWeatherService.updateByLocationCode(locationCode,hourlyWeatherList);

        if(updatedHourlyWeather.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(addLinksByLocation(weatherMapper.listHourlyEntity2DTO(updatedHourlyWeather),locationCode));
    }


    private HourlyWeatherListDTO addLinksByIP(HourlyWeatherListDTO hourlyWeatherDTO){
        hourlyWeatherDTO.add(
                linkTo(methodOn(HourlyWeatherApiController.class)
                        .listHourlyForecastByIPAddress(null))
                        .withSelfRel());
        hourlyWeatherDTO.add(
                linkTo(methodOn(RealTimeWeatherController.class)
                        .getRealTimeWeatherByIPAddress(null))
                        .withRel("realtime_weather"));
        hourlyWeatherDTO.add(
                linkTo(methodOn(DailyWeatherController.class)
                        .listDailyWeatherByIpAddress(null))
                        .withRel("daily_forecast"));

        hourlyWeatherDTO.add(
                linkTo(methodOn(FullWeatherApiController.class)
                        .getFullWeatherByIPAddress(null))
                        .withRel("full_forecast"));
        return hourlyWeatherDTO;
    }

    private HourlyWeatherListDTO addLinksByLocation(HourlyWeatherListDTO hourlyWeatherDTO,String locationCode){
        hourlyWeatherDTO.add(
                linkTo(methodOn(HourlyWeatherApiController.class)
                        .listHourlyForecastByLocationCode(locationCode,null))
                        .withSelfRel());
        hourlyWeatherDTO.add(
                linkTo(methodOn(RealTimeWeatherController.class)
                        .getRealTimeWeatherByLocationCode(locationCode))
                        .withRel("realtime_weather"));
        hourlyWeatherDTO.add(
                linkTo(methodOn(DailyWeatherController.class)
                        .listDailyForecastByLocationCode(locationCode))
                        .withRel("daily_forecast"));

        hourlyWeatherDTO.add(
                linkTo(methodOn(FullWeatherApiController.class)
                        .getFullWeatherByLocationCode(locationCode))
                        .withRel("full_forecast"));
        return hourlyWeatherDTO;
    }






}
