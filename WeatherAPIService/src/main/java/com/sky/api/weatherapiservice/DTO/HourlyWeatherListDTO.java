package com.sky.api.weatherapiservice.DTO;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyWeatherListDTO extends RepresentationModel<HourlyWeatherListDTO> {

    private String location;

    @Builder.Default
    private List<HourlyWeatherDTO> hourlyWeatherList = new ArrayList<>();

    // Optional helper, still safe with @Builder.Default
    public void addHourlyWeatherDTO(HourlyWeatherDTO hourlyWeatherDTO) {
        hourlyWeatherList.add(hourlyWeatherDTO);
    }
}

