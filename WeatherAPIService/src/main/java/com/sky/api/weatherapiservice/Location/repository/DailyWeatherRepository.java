package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.DailyWeather;
import com.sky.api.weatherapicommon.entity.DailyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyWeatherRepository extends JpaRepository<DailyWeather, DailyWeatherId> {

    @Query("SELECT d FROM DailyWeather d where d.location.code=:code")
    List<DailyWeather> findByLocationCode(@Param("code") String locationCode);
}
