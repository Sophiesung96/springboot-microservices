package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.HourlyWeatherRepository;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HourlyWeatherService {

    private final LocationRepository locationRepository;
    private HourlyWeatherRepository hourlyWeatherRepository;
    @Autowired
    public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepository, LocationRepository locationRepository) {
        this.hourlyWeatherRepository=hourlyWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public List<HourlyWeather> getByLocation(Location location, int currentHour) {
        String locationCode=location.getCode();
        String countryCode=location.getCountryCode();
        String cityName=location.getCityName();
        log.info("Location code:{}, countryCode:{}, cityName:{}", locationCode, countryCode, cityName);
        Location locationInDB=locationRepository.findByCountryCodeCityName(countryCode, cityName);
        if(locationInDB==null) {
            throw new LocationNotFoundException("No location found for code: "+countryCode+" and cityName: "+cityName);
        }
        return hourlyWeatherRepository.findByLocationCodeAndHour(locationCode,currentHour);
    }

    public Optional<List<HourlyWeather>> getByLocationCode(String locationCode, int currentHour) {
        return Optional.ofNullable(locationRepository.findByCode(locationCode))
                .map(location -> hourlyWeatherRepository.findByLocationCodeAndHour(locationCode, currentHour));
    }

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeatherList){
        Location locationDB=locationRepository.findByCode(locationCode);
        if(locationDB==null)
        {
            throw new LocationNotFoundException("No location was found with this given code: "+locationCode);
        }
        // Ensure all entities match the location
        boolean allMatchLocation = hourlyWeatherList.stream()
                .allMatch(hw -> hw.getLocation().getCode().equals(locationCode));
        if (!allMatchLocation) {
            throw new IllegalArgumentException("All HourlyWeather entities must match the given location code: " + locationCode);
        }


        return (List<HourlyWeather>) hourlyWeatherRepository.saveAll(hourlyWeatherList);
    }

}
