package com.sky.api.weatherapiservice.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import com.sky.api.weatherapiservice.Filter.RealtimeWeatherFieldFilter;
import jakarta.validation.Valid;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FullWeatherDTO {
    private String location;

    @JsonProperty("realtime_weather")
    @JsonInclude(value=JsonInclude.Include.CUSTOM,valueFilter = RealtimeWeatherFieldFilter.class)
    @Valid
    private RealtimeWeatherDTO realtimeWeather;
    @JsonProperty("hourly_forecast")
    @Valid
    private HourlyWeatherListDTO hourlyWeather=new HourlyWeatherListDTO();
    @JsonProperty("daily_forecast")
    private DailyWeatherListDTO dailyWeather=new DailyWeatherListDTO();

}
