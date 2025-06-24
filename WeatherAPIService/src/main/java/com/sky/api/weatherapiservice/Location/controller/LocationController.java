package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.DTO.LocationDTO;
import com.sky.api.weatherapiservice.Exception.BadRequestException;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/v1/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    private  WeatherMapper weatherMapper;

    private final Map<String, String> propertyMap = Map.of(
            "code", "code",
            "city_name", "cityName",
            "region_name", "regionName",
            "country_name", "countryName",
            "enabled", "enabled"
    );

    public LocationController(LocationService locationService, WeatherMapper weatherMapper) {
        this.locationService = locationService;
        weatherMapper=weatherMapper;
    }

    @PostMapping
    public ResponseEntity<LocationDTO>addLocation(@Valid @RequestBody Location location) {

        Location theLocation=locationService.add(location);
        URI uri=URI.create("v1/locations/"+theLocation.getCode());

        LocationDTO locationDTO=weatherMapper.mapEntity2DTO(theLocation);
        return ResponseEntity.created(uri).body(addLinks2Item(locationDTO));
    }

   @Deprecated
    public ResponseEntity<?> listAllLocations() {
        List<Location> list=locationService.list();
        if(list.isEmpty())
        {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(list);
    }

    @GetMapping
    public ResponseEntity<?> listLocations(@RequestParam(value="page",required = false,defaultValue = "1") @Min(value=1) Integer pageNum,
                                           @RequestParam(value="size", required = false, defaultValue = "5") @Min(value = 5) @Max(value=20)Integer pageSize,
                                           @RequestParam(value = "sort", required = false,defaultValue = "code") String sortField) throws BadRequestException {

        if(!propertyMap.containsKey(sortField)){
            throw new BadRequestException("invalid sort field "+sortField);
        }
        Page<Location> page=locationService.listByPage(pageNum-1,pageSize,sortField);
        if(page.getContent().isEmpty() || page.getContent()==null)
        {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(page.getContent());
    }


    private CollectionModel<LocationDTO> addPageMetadata(List<LocationDTO> listDto,Page pageInfo){

    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getLocation(@PathVariable  String code) {

        Location location=locationService.get(code);
        return ResponseEntity.ok().body(addLinks2Item(weatherMapper.mapEntity2DTO(location)));
    }


    @PutMapping
    public ResponseEntity<?> updateLocation(@RequestBody Location location) {

            Location updatedLocation=locationService.update(location);
            return ResponseEntity.ok().body(addLinks2Item(weatherMapper.mapEntity2DTO(updatedLocation)));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable String code)
    {
            locationService.deleteLocation(code);
            return ResponseEntity.noContent().build();
    }


    private LocationDTO addLinks2Item(LocationDTO dto) {

        dto.add(linkTo(
                methodOn(LocationController.class).getLocation(dto.getCode()))
                .withSelfRel());

        dto.add(linkTo(
                methodOn(RealTimeWeatherController.class).getRealTimeWeatherByLocationCode(dto.getCode()))
                .withRel("realtime_weather"));

        dto.add(linkTo(
                methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(dto.getCode(), null))
                .withRel("hourly_forecast"));

        dto.add(linkTo(
                methodOn(DailyWeatherController.class).listDailyForecastByLocationCode(dto.getCode()))
                .withRel("daily_forecast"));

        dto.add(linkTo(
                methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(dto.getCode()))
                .withRel("full_forecast"));

        return dto;
    }

}
