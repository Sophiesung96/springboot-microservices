package com.sky.api.weatherapicommon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyWeatherId implements Serializable {
    private int dayOfMonth;
    private int month;
    @Column(name = "location_code")
    private String locationCode;

    // Override equals() and hashCode() for correct entity identity management
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyWeatherId that = (DailyWeatherId) o;
        return dayOfMonth == that.dayOfMonth &&
                month == that.month &&
                Objects.equals(locationCode, that.locationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfMonth, month, locationCode);
    }

    public DailyWeatherId deepCopy(){
        return new DailyWeatherId(dayOfMonth, month, locationCode);
    }
}
