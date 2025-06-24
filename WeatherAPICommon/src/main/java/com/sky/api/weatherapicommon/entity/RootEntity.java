package com.sky.api.weatherapicommon.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;
@Data
@JsonPropertyOrder({"locations_url","location_by_code_url","reatimeWeather_by_ip_u",
"realtimeWeather_by_code_url","hourly_forecast_by_ip_url","hourly_forecast_by_code_url","daily_forecast_by_ip_url",
"daily_forecast_by_code_url","full_weather_by_ip_url","full_weather_by_code_url"})
public class RootEntity {

    private String locationsUrl;

    private String locationByCodeUrl;

    private String realtimeWeatherByIpUrl;


    private String realtimeWeatherByCodeUrl;


    private String hourlyForecastByIpUrl;


    private String hourlyForecastByCodeUrl;

    private String dailyForecastByIpUrl;

    private String dailyForecastByCodeUrl;

    private String fullWeatherByIpUrl;

    private String fullWeatherByCodeUrl;



}
