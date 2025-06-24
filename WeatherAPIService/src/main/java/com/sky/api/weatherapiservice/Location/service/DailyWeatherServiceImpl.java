package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.DailyWeather;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.DailyWeatherRepository;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DailyWeatherServiceImpl implements DailyWeatherService{

    private DailyWeatherRepository dailyWeatherRepository;
    private LocationRepository locationRepository;

    public DailyWeatherServiceImpl(DailyWeatherRepository dailyWeatherRepository,
                                   LocationRepository locationRepository) {
        this.dailyWeatherRepository = dailyWeatherRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional
    public List<DailyWeather> getByLocationCode(String locationCode) {


        Location dbLocation=locationRepository.findByCode(locationCode);

        if(dbLocation==null){
            throw new LocationNotFoundException(locationCode);
        }
        return dailyWeatherRepository.findByLocationCode(dbLocation.getCode());
    }
    @Override
    @Transactional
    public List<DailyWeather> getByLocation(Location location) {

        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository.findByCountryCodeCityName(countryCode, cityName);

        if (locationInDB == null) {
            throw new LocationNotFoundException(countryCode, cityName);
        }

        return dailyWeatherRepository.findByLocationCode(locationInDB.getCode());
    }

    @Override
    @Transactional
    public List<DailyWeather> updateByLocationCode(String locationCode, List<DailyWeather> dailyWeatherListInRequest) {
        Location location=locationRepository.findByCode(locationCode);
        if(location==null){
            throw new LocationNotFoundException(locationCode);
        }
        dailyWeatherListInRequest.forEach(daily-> daily.setLocation(location));
        List<DailyWeather> dailyWeatherListInDB=location.getListDailyWeather();
        List<DailyWeather> dailyWeatherToBeRemoved=new ArrayList<>();
        dailyWeatherListInDB.forEach(daily-> {
            if(!dailyWeatherListInRequest.contains(daily)){
                dailyWeatherToBeRemoved.add(daily.deepCopy());
            }
        });
        dailyWeatherListInRequest.forEach(request->{
            dailyWeatherListInDB.remove(request);
        });
        return dailyWeatherRepository.saveAll(dailyWeatherListInRequest);
    }
}
