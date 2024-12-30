package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface RealTimeWeatherRepository extends CrudRepository<RealTimeWeather, String> {

    @Query("select r from RealTimeWeather r where r.location.countryCode = :countryCode and r.location.cityName = :city")
     RealTimeWeather findByCountryCodeAndCity(@Param(value = "countryCode") String countryCode, @Param(value = "city") String city);

    @Query("select r from RealTimeWeather  r where r.locationCode=:locationcode")
    RealTimeWeather findByLocationCode(@Param(value = "locationcode") String locationCode);
}

