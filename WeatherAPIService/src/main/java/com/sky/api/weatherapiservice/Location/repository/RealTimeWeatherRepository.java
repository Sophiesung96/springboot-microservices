package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealTimeWeatherRepository extends CrudRepository<RealTimeWeather,String> {

}
