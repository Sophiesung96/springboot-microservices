package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import com.sky.api.weatherapiservice.Location.repository.RealTimeWeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class RealTimeWeatherService {

    private RealTimeWeatherRepository realTimeWeatherRepository;
    private LocationRepository locationRepository;

    @Autowired
    public RealTimeWeatherService( RealTimeWeatherRepository realTimeWeatherRepository,  LocationRepository locationRepository) {
        this.realTimeWeatherRepository = realTimeWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public RealTimeWeather getByLocation(Location location){
        String countryCode=location.getCountryCode();
        String cityName=location.getCityName();
        RealTimeWeather realTimeWeather=realTimeWeatherRepository.findByCountryCodeAndCity(countryCode,cityName);
        log.info("Searching weather for countryCode: {} and city: {}", countryCode, cityName);
        if(realTimeWeather==null)
        {
            log.error("No data found for countryCode: {} and city: {}", countryCode, cityName);
            throw new LocationNotFoundException("No location found with the given country code and city name");
        }
        return realTimeWeather;
    }


    public RealTimeWeather getByLocationCode(String locationCode){

        RealTimeWeather realTimeWeather=realTimeWeatherRepository.findByLocationCode(locationCode);
        if(realTimeWeather==null)
        {
            log.error("No data found for locationCode: {}", locationCode);
            throw new LocationNotFoundException("No location found with the given location code");
        }
        return realTimeWeather;
    }

    @Transactional
    public RealTimeWeather update(String locationCode,RealTimeWeather realTimeWeather)
    {
        Location location=locationRepository.findByCode(locationCode);
        if(location==null)
        {
            throw new LocationNotFoundException("No location found with the given location code");
        }
        if(location.getRealTimeWeather()==null)
        {
            realTimeWeather.setLocation(location);
            realTimeWeather.setLastUpdated(new Date());
            Location updatedLocation=locationRepository.save(location); //ensures the  that the association between the Location and the RealTimeWeather entities
                                                                        // is established and persisted
            return updatedLocation.getRealTimeWeather();
        }
        return realTimeWeatherRepository.save(realTimeWeather);
    }

}
