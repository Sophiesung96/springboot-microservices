package com.sky.api.weatherapiservice.mapper;

import com.sky.api.weatherapicommon.entity.*;
import com.sky.api.weatherapiservice.DTO.*;
import com.sky.api.weatherapiservice.Exception.DailyWeatherForecastException;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WeatherMapper {



    private final LocationService locationService;

    public WeatherMapper(LocationService locationService) {
        this.locationService = locationService;
    }


    public LocationDTO mapEntity2DTO(Location location){
        return LocationDTO.builder()
                .code(location.getCode())
                .regionName(location.getRegionName())
                .cityName(location.getCityName())
                .countryCode(location.getCountryCode())
                .countryName(location.getCountryName())
                .enabled(location.isEnabled())
                .build();
    }

    public Location mapDTO2Entity (LocationDTO locationDTO){
        return Location.builder()
                .regionName(locationDTO.getRegionName())
                .cityName(locationDTO.getCityName())
                .countryCode(locationDTO.getCountryCode())
                .countryName(locationDTO.getCountryName())
                .enabled(locationDTO.isEnabled())
                .build();
    }

    public List<LocationDTO> listEntity2ListDTO(List<Location> listEntity) {

        return listEntity.stream().map(this::mapEntity2DTO)
                .collect(Collectors.toList());

    }


    //helper method
    public DailyWeather mapDTO2Entity(DailyWeatherDTO dailyWeatherDTO, Location location) {

        DailyWeatherId dailyWeatherId=new DailyWeatherId(dailyWeatherDTO.getDayOfMonth(),dailyWeatherDTO.getMonth(),location.getCode());
        return DailyWeather.builder()
                .dailyWeatherId(dailyWeatherId)
                .minTemp(dailyWeatherDTO.getMinTemp()) // min temperature
                .maxTemp(dailyWeatherDTO.getMaxTemp()) // max temperature
                .precipitation(dailyWeatherDTO.getPrecipitation()) // Map precipitation
                .location(location) // Use the fetched Location object
                .status(dailyWeatherDTO.getStatus())
                .build();
    }

    //helper method
    public DailyWeatherListDTO listDailyEntity2DTO(List<DailyWeather> dailyWeatherList) {
        if (dailyWeatherList == null || dailyWeatherList.isEmpty()) {
            throw new DailyWeatherForecastException("DailyWeather list cannot be null or empty");
        }

        Location location = dailyWeatherList.get(0).getLocation();

        List<DailyWeatherDTO> dtoList = dailyWeatherList.stream()
                .map(this::mapDailyEntity2DTO)
                .collect(Collectors.toList());

        return DailyWeatherListDTO.builder()
                .location(location.toString())
                .dailyWeatherDTOList(dtoList)
                .build();
    }

    private DailyWeatherDTO mapDailyEntity2DTO(DailyWeather dailyWeather){
        return DailyWeatherDTO.builder()
                .dayOfMonth(dailyWeather.getDailyWeatherId().getDayOfMonth())
                .month(dailyWeather.getDailyWeatherId().getMonth())
                .minTemp(dailyWeather.getMinTemp())
                .maxTemp(dailyWeather.getMaxTemp())
                .precipitation(dailyWeather.getPrecipitation())
                .status(dailyWeather.getStatus())
                .build();
    }

    private List<DailyWeather> listHourlyDTO2Entity(DailyWeatherListDTO dailyWeatherListDTO) {
        // Validate input
        if (dailyWeatherListDTO == null || dailyWeatherListDTO.getDailyWeatherDTOList() == null || dailyWeatherListDTO.getDailyWeatherDTOList().isEmpty()) {
            throw new IllegalArgumentException("dailyWeatherListDTO list cannot be null or empty");
        }
        // Extract the location code from the DTO
        String locationCode = dailyWeatherListDTO.getLocation();

        // Fetch the location from the service
        Location location = locationService.findByLocationCode(locationCode);

        // Map each DTO to an entity
        return dailyWeatherListDTO.getDailyWeatherDTOList().stream() // Extract the internal list
                .map(dto -> mapDTO2Entity(dto, location)) // Use helper method for mapping
                .collect(Collectors.toList());
    }


    public HourlyWeatherListDTO listHourlyEntity2DTO(List<HourlyWeather> entity) {
        // Extract the location code from the first entity
        String location = entity.get(0).getLocation().toString();

        // Map the list of entities to DTOs
        List<HourlyWeatherDTO> hourlyWeatherDTOList = entity.stream()
                .map(this::mapHourlyEntity2DTO) // Use the mapper method for each item
                .collect(Collectors.toList());

        // Build and return the DTO
        return HourlyWeatherListDTO.builder()
                .location(location)
                .hourlyWeatherList(hourlyWeatherDTOList)
                .build();
    }


    private HourlyWeatherDTO mapHourlyEntity2DTO(HourlyWeather entity) {
        return HourlyWeatherDTO.builder()
                .precipitation(entity.getPrecipitation()) // Ensure correct field mapping
                .status(entity.getLocation().getRealTimeWeather().getStatus()) // Get status from RealTimeWeather
                .hourOfDay(entity.getId().getHourOfDay()) // Map hourOfDay
                .temperature(entity.getTemperature()) // Map temperature
                .build();
    }


    public List<HourlyWeather> listHourlyDTO2Entity(HourlyWeatherListDTO hourlyWeatherDTOList) {
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

    public HourlyWeatherListDTO mapDTOListToHourlyWeatherDTOList(List<HourlyWeatherDTO> hourlyWeatherDTOList, String locationCode) {
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


    public FullWeatherDTO fullEntity2Dto(Location location){



        FullWeatherDTO dto= FullWeatherDTO.builder()
                .location(location.toString())
                .realtimeWeather(mapRealEntiyToDTO(location.getRealTimeWeather()))
                .dailyWeather(listDailyEntity2DTO(location.getListDailyWeather()))
                .hourlyWeather(listHourlyEntity2DTO(location.getListHourlyWeather()))
                .build();
        return dto;
    }


    public Location fullDtoToLocationEntity(FullWeatherDTO fullWeatherDTO){

        Location location= Location.builder()
                .code(fullWeatherDTO.getLocation())
                .realTimeWeather(mapDTOtoEntity(fullWeatherDTO.getRealtimeWeather()))
                .listDailyWeather(listHourlyDTO2Entity(fullWeatherDTO.getDailyWeather()))
                .listHourlyWeather(listHourlyDTO2Entity(fullWeatherDTO.getHourlyWeather()))
                .build();
        return location;
    }


    public RealtimeWeatherDTO mapRealEntiyToDTO(RealTimeWeather realTimeWeather){
        return RealtimeWeatherDTO.builder()
                .humidity(realTimeWeather.getHumidity())
                .windSpeed(realTimeWeather.getWindSpeed())
                .temperature(realTimeWeather.getTemperature())
                .lastUpdated(realTimeWeather.getLastUpdated())
                .status(realTimeWeather.getStatus())
                .location(realTimeWeather.getLocationCode())
                .precipitation(realTimeWeather.getPrecipitation())
                .build();
    }

    public RealTimeWeather mapDTOtoEntity(RealtimeWeatherDTO dto){

        RealTimeWeather real= RealTimeWeather.builder()
                .status(dto.getStatus())
                .locationCode(dto.getLocation())
                .temperature(dto.getTemperature())
                .windSpeed(dto.getWindSpeed())
                .humidity(dto.getHumidity())
                .precipitation(dto.getPrecipitation())
                .lastUpdated(dto.getLastUpdated())
                .build();
        return real;
    }



}
