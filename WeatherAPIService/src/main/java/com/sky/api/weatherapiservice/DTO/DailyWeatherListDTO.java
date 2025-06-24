package com.sky.api.weatherapiservice.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyWeatherListDTO {

    private String location;
    @Builder.Default
    private List<DailyWeatherDTO> dailyWeatherDTOList=new ArrayList<>();

    public void addDailyWeatherDTO(DailyWeatherDTO dailyWeatherDTO) {
        this.dailyWeatherDTOList.add(dailyWeatherDTO);
    }

}
