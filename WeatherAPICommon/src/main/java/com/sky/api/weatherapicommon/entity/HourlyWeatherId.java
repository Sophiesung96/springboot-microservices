package com.sky.api.weatherapicommon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class HourlyWeatherId implements Serializable {

    private int hourOfDay;

    @Column(name = "location_code")
    private String locationCode; // Use String instead of Location entity


    public HourlyWeatherId() {
    }

    public HourlyWeatherId(int hourOfDay, String locationCode) {
        this.hourOfDay = hourOfDay;
        this.locationCode = locationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HourlyWeatherId that)) return false;
        return hourOfDay == that.hourOfDay &&
                Objects.equals(locationCode, that.locationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hourOfDay, locationCode);
    }

}
