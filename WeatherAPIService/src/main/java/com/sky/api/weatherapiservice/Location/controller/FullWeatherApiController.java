package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.DTO.FullWeatherDTO;
import com.sky.api.weatherapiservice.DTO.FullWeatherModelAssembler;
import com.sky.api.weatherapiservice.Exception.DailyWeatherForecastException;
import com.sky.api.weatherapiservice.Exception.HourlyWeatherForecastException;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import com.sky.api.weatherapiservice.Location.service.FullWeatherService;
import com.sky.api.weatherapiservice.Location.service.GeoLocationService;
import com.sky.api.weatherapiservice.Utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Entity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/full")
@Slf4j
@RequiredArgsConstructor
public class FullWeatherApiController {

    private final FullWeatherService fullWeatherService;
    private final GeoLocationService geoLocationService;
    private final WeatherMapper weatherMapper;
    @Autowired
    private final FullWeatherModelAssembler assembler;

    @GetMapping("")
    public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress= CommonUtility.getIpAddress(request);
        Location locationFromIP=geoLocationService.getLocation(ipAddress);
        Location locationFromDB=fullWeatherService.getByLocation(locationFromIP);

        return ResponseEntity.ok(assembler.toModel(weatherMapper.fullEntity2Dto(locationFromDB)));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable String locationCode){
        Location locationInDB=fullWeatherService.get(locationCode);
        return ResponseEntity.ok(addLinksByLocation(weatherMapper.fullEntity2Dto(locationInDB),locationCode));

    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateFullWeather(@PathVariable String locationCode,
                                               @Valid  @RequestBody FullWeatherDTO fullWeatherDTO) throws BadRequestException {

        if(fullWeatherDTO.getHourlyWeather().getHourlyWeatherList().isEmpty()){
            throw new HourlyWeatherForecastException("Hourly Weather data cannot be empty");
        }
        if(fullWeatherDTO.getDailyWeather().getDailyWeatherDTOList().isEmpty())
        {
            throw new DailyWeatherForecastException("Daily Weather data cannot be empty");
        }

        Location locationFromRequest=weatherMapper.fullDtoToLocationEntity(fullWeatherDTO);
        Location updatedLocation=fullWeatherService.update(locationCode,locationFromRequest);
        FullWeatherDTO updatedDTO=weatherMapper.fullEntity2Dto(updatedLocation);


        return ResponseEntity.ok(addLinksByLocation(updatedDTO,locationCode));

    }


    private EntityModel<FullWeatherDTO> addLinksByLocation(FullWeatherDTO dto, String locationCode){
        return EntityModel.of(dto)
                .add(linkTo(methodOn(FullWeatherApiController.class)
                        .getFullWeatherByLocationCode(locationCode)).withSelfRel());
    }







}
