package com.sky.api.weatherapicommon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "daily_weather")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyWeather {
    @EmbeddedId
    private DailyWeatherId dailyWeatherId;
    private int minTemp;
    private int maxTemp;
    private int precipitation;
    @Column(name = "status",length = 50)
    private String status;
    @ManyToOne()
    @JoinColumn(name = "location_code",insertable = false, updatable = false)
    private Location location;

    public DailyWeather deepCopy(){
        DailyWeather dailyWeather=DailyWeather
                .builder()
                .dailyWeatherId(this.dailyWeatherId!=null ? this.dailyWeatherId.deepCopy(): null)
                .status(this.status)
                .precipitation(this.precipitation)
                .maxTemp(this.maxTemp)
                .minTemp(this.minTemp)
                .build();
        return dailyWeather;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyWeather that = (DailyWeather) o;
        return Objects.equals(dailyWeatherId, that.dailyWeatherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dailyWeatherId);
    }


}
