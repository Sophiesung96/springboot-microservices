package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.HourlyWeatherId;
import com.sky.api.weatherapicommon.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class HourlyWeatherRepositoryTest {

    @Autowired
    private HourlyWeatherRepository hourlyWeatherRepository;

    @Test
    public void testAdd(){

        Location location= Location.builder().code("NYC_USA").build();
        HourlyWeatherId forecastId2 = new HourlyWeatherId();
        forecastId2.setHourOfDay(11);
        forecastId2.setLocationCode(location.getCode());
        HourlyWeather forecast2 = HourlyWeather.builder()
                .id(forecastId2)
                .temperature(16)
                .precipitation(50)
                .location(location)
                .build();

        HourlyWeather hourlyWeather=hourlyWeatherRepository.save(forecast2);
        assertNotNull(hourlyWeather);
    }

    @Test
    public void testDelete(){
        Location location= Location.builder().code("NYC_USA").build();
        HourlyWeatherId forecastId2 = new HourlyWeatherId();
        forecastId2.setHourOfDay(11);
        forecastId2.setLocationCode(location.getCode());
        HourlyWeather hourlyWeather=hourlyWeatherRepository.findById(forecastId2).get();
        hourlyWeatherRepository.delete(hourlyWeather);
    }

    @Test
    public void testFindByCountryCodeAndCityFound(){
        String locationCode="DELHL_IN";
        List<HourlyWeather> hourlyWeatherList=hourlyWeatherRepository.findByLocationCodeAndHour(locationCode,9);
        assertNotNull(hourlyWeatherList);
        hourlyWeatherList.stream().forEach(hourlyWeather -> System.out.println(hourlyWeather.toString()));
    }

}