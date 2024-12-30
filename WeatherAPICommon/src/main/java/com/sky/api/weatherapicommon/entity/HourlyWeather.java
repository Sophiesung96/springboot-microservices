package com.sky.api.weatherapicommon.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "weather_hourly")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyWeather {

    @EmbeddedId
    private HourlyWeatherId id;

    @ManyToOne
    @JoinColumn(name = "location_code", insertable = false, updatable = false)
    private Location location;

    private int temperature;

    private int precipitation;
}

