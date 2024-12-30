package com.sky.api.weatherapiservice.DTO;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class HourlyWeatherListDTO {

    private String location;

    private List<HourlyWeatherDTO> hourlyWeatherList;

    public void addHourlyWeatherDTO(HourlyWeatherDTO hourlyWeatherDTO) {
        if(hourlyWeatherList == null) {
            hourlyWeatherList = new ArrayList<HourlyWeatherDTO>();
        }
        hourlyWeatherList.add(hourlyWeatherDTO);
    }
}
