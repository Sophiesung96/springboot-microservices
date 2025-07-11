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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    private  final WeatherMapper weatherMapper;

    private final Map<String, String> propertyMap = Map.of(
            "code", "code",
            "city_name", "cityName",
            "region_name", "regionName",
            "country_name", "countryName",
            "enabled", "enabled"
    );


    @PostMapping
    public ResponseEntity<LocationDTO>addLocation(@Valid @RequestBody LocationDTO location) {

        Location theLocation=weatherMapper.mapDTO2Entity(location);
        Location locationDB=locationService.add(theLocation);
        URI uri=URI.create("v1/locations/"+theLocation.getCode());

        LocationDTO locationDTO=weatherMapper.mapEntity2DTO(locationDB);
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

    private Sort parseSortOption(String sortOption) throws BadRequestException {
        String[] fields = sortOption.split(",");
        List<Sort.Order> orders = new ArrayList<>();

        for (String field : fields) {
            boolean descending = field.startsWith("-");
            log.info("Order sequence:{}",descending);
            String apiFieldName = field.replaceFirst("^-", "");

            if (!propertyMap.containsKey(apiFieldName)) {
                throw new BadRequestException("Invalid sort field: " + apiFieldName);
            }

            String entityField = propertyMap.get(apiFieldName);
            Sort.Order order = new Sort.Order(descending ? Sort.Direction.DESC : Sort.Direction.ASC, entityField);
            log.info("Sort order added: {} {}", order.getProperty(), order.getDirection());
            orders.add(order);
        }

        return Sort.by(orders);
    }


    @GetMapping
    public ResponseEntity<?> listLocations(@RequestParam(value="page",required = false,defaultValue = "1") @Min(value=1) Integer pageNum,
                                           @RequestParam(value="size", required = false, defaultValue = "5") @Min(value = 5) @Max(value=20)Integer pageSize,
                                           @RequestParam(value = "sort", required = false,defaultValue = "code") String sortOption,
                                           @RequestParam(value = "enabled", required = false,defaultValue = "") String enabled,
                                           @RequestParam(value = "region_name", required = false,defaultValue = "") String regionName,
                                           @RequestParam(value = "country_code", required = false,defaultValue = "") String countryCode) throws BadRequestException {


        Map<String,Object> filterFields=new HashMap<>();
        if(!"".equals(enabled))
        {
            filterFields.put("enabled",enabled);
        }
        if(!"".equals(regionName))
        {
            filterFields.put("regionName",regionName);
        }
        if(!"".equals(countryCode))
        {
            filterFields.put("countryCode",countryCode);
        }
        Sort sort = parseSortOption(sortOption);
        Page<Location> page=locationService.listByPage(pageNum-1,pageSize,sort,filterFields);
        List<Location> location=page.getContent();
        if(location.isEmpty() || page.getContent()==null)
        {
            return ResponseEntity.noContent().build();
        }

          HttpHeaders header=new HttpHeaders();
        header.setCacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES).cachePublic());
//        header.setExpires(Instant.now().plus(7, ChronoUnit.DAYS));
        var responseBody=addPageMetadataAndLinks2Collection(weatherMapper.listEntity2ListDTO(location),page,sortOption,enabled,regionName,countryCode);

        return new ResponseEntity<>(responseBody,header,HttpStatus.OK);
    }


    private CollectionModel<LocationDTO> addPageMetadataAndLinks2Collection(List<LocationDTO> listDto
            ,Page<Location> pageInfo,String sortField,String enabled, String countryCode,

                                                                            String regionName) throws BadRequestException {
        String actualEnabled=!enabled.equals("")? null:enabled;
        String actualRegionName=!regionName.equals("")? null:regionName;
        String actualCountryCode=!countryCode.equals("")? null:countryCode;
        // add self link to each individual item
        listDto.stream().map(dto-> dto.add(linkTo(methodOn(LocationController.class).getLocation(dto.getCode())).withSelfRel()));
        int pageSize= pageInfo.getSize();
        int pageNum= pageInfo.getNumber()+1;
        long totalElements=pageInfo.getTotalElements();
        int totalPages= pageInfo.getTotalPages();
        PagedModel.PageMetadata pageMetadata=new PagedModel.PageMetadata(pageSize,pageNum,totalElements,pageInfo.getTotalPages());
        CollectionModel<LocationDTO> model=PagedModel.of(listDto,pageMetadata);
        // add self link to collection
        model.add(linkTo(methodOn(LocationController.class).listLocations(pageNum,pageSize,sortField,actualEnabled,actualRegionName,actualCountryCode)).withSelfRel());
        if(pageNum > 1)
        {
            // add link to the first page if the current page is not the first one
            model.add(linkTo(methodOn(LocationController.class)
                    .listLocations(1,pageSize,sortField,actualEnabled,actualRegionName,actualCountryCode)).withRel(IanaLinkRelations.FIRST));
            // add link to the previous page if the current page is not the first one
            model.add(linkTo(methodOn(LocationController.class)
                    .listLocations(pageNum-1,pageSize,sortField,actualEnabled,actualRegionName,actualCountryCode)).withRel(IanaLinkRelations.PREVIOUS));
           if(pageNum < totalPages)
           {
               // add link to the next page if the current page is not the first one
               model.add(linkTo(methodOn(LocationController.class)
                       .listLocations(pageNum+1,pageSize,sortField,actualEnabled,actualRegionName,actualCountryCode)).withRel(IanaLinkRelations.NEXT));
               // add link to the last page if the current page is not the first one
               model.add(linkTo(methodOn(LocationController.class)
                       .listLocations(totalPages,pageSize,sortField,actualEnabled,actualRegionName,actualCountryCode)).withRel(IanaLinkRelations.LAST));
           }
        }
        return model;

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
