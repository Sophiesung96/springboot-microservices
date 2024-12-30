package com.sky.api.weatherapiservice.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HourlyWeatherDTO {

    private int hourOfDay;

    private int temperature;

    private int precipitation;

    private String status;

}
