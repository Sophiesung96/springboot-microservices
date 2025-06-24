package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.DailyWeather;
import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class FullWeatherServiceImpl implements FullWeatherService{

    private LocationRepository locationRepository;

    public FullWeatherServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location getByLocation(Location locationFromIP) {
        String cityName=locationFromIP.getCityName();
        String countryName=locationFromIP.getCountryName();
        Location locationDB=locationRepository.findByCountryCodeCityName(countryName,cityName);
        if(locationDB==null){
            throw new LocationNotFoundException(countryName,cityName);
        }
        return locationDB;
    }

    @Override
    public Location get(String locationCode) {
        Location locationInDB=locationRepository.findByCode(locationCode);
        if(locationInDB==null){
            throw new LocationNotFoundException(locationCode);
        }
        return locationInDB;
    }

    @Override
    @Transactional
    public Location update(String locationCode, Location locationInRequest) {

        Location locationInDB=locationRepository.findByCode(locationCode);
        if(locationInDB==null){
            throw new LocationNotFoundException(locationCode);
        }
        RealTimeWeather realTimeWeather=locationInDB.getRealTimeWeather();
        realTimeWeather.setLocation(locationInDB);
        realTimeWeather.setLastUpdated(new Date());
        if(locationInDB.getRealTimeWeather()==null){
            locationInDB.setRealTimeWeather(realTimeWeather);
        }
        List<DailyWeather> listDailyWeather=locationInRequest.getListDailyWeather();
        listDailyWeather.forEach(dw-> dw.setLocation(locationInDB));
        List<HourlyWeather> hourlyWeatherList=locationInRequest.getListHourlyWeather();
        hourlyWeatherList.forEach(hr-> hr.setLocation(locationInDB));
        locationInRequest.setCode(locationInDB.getCode());
        locationInRequest.setCityName(locationInDB.getCityName());
        locationInRequest.setCountryCode(locationInDB.getCountryCode());
        locationInRequest.setCountryName(locationInDB.getCountryName());
        locationInRequest.setEnabled(locationInDB.isEnabled());
        locationInRequest.setRegionName(locationInDB.getRegionName());
        locationInRequest.setTrashed(locationInRequest.isTrashed());
        return locationRepository.save(locationInRequest);
    }
}
