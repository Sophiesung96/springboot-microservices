package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Location.service.RealTimeWeatherService;
import com.sky.api.weatherapiservice.Utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/realtime")
@Slf4j
public class RealTimeWeatherController {

    private RealTimeWeatherService realTimeWeatherService;
    private GeoLocationService geoLocationService;


    public RealTimeWeatherController(@Autowired RealTimeWeatherService realTimeWeatherService,
                                     @Autowired GeoLocationService geoLocationService) {
        this.realTimeWeatherService = realTimeWeatherService;
        this.geoLocationService = geoLocationService;
    }

    @GetMapping
    public ResponseEntity<?> getRealTimeWeatherByIPAddress(HttpServletRequest request){
       String ipAddress= CommonUtility.getIpAddress(request);
       Location locationFromIP=geoLocationService.getLocation(ipAddress);
       RealTimeWeather realTimeWeather=realTimeWeatherService.getByLocation(locationFromIP);
       return ResponseEntity.ok(realTimeWeather);
    }


    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealTimeWeatherByLocationCode(@PathVariable String locationCode){

        RealTimeWeather realTimeWeather=realTimeWeatherService.getByLocationCode(locationCode);
        return ResponseEntity.ok(realTimeWeather);

    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealTimeWeather(@PathVariable String locationCode,@RequestBody @Valid RealTimeWeather realTimeWeather){

        RealTimeWeather updatedRealtimeWeather=realTimeWeatherService.update(locationCode,realTimeWeather);
        return ResponseEntity.ok(updatedRealtimeWeather);

    }

}
