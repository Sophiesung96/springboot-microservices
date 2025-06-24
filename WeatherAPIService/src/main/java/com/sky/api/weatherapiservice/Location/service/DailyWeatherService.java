package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.DailyWeather;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.Location.repository.DailyWeatherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DailyWeatherService {

    public List<DailyWeather> getByLocationCode(String locationCode);
    public List<DailyWeather> getByLocation(Location location);
    public List<DailyWeather> updateByLocationCode(String locationCode, List<DailyWeather> dailyWeatherList);
}
