package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.HourlyWeatherId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather, HourlyWeatherId> {


    @Query("SELECT h FROM HourlyWeather h WHERE h.id.locationCode = :locationCode AND h.id.hourOfDay > :currentHour and h.location.trashed=false")
    List<HourlyWeather> findByLocationCodeAndHour(@Param(value = "locationCode") String locationCode, @Param(value = "currentHour") int currentHour);


}
