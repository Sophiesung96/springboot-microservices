package com.sky.api.weatherapicommon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "realtime_weather")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeWeather {

    @Id
    @Column(name = "location_code")
    //it also serves as the FK of the Location entity class
    //The locationCode field must match the id of the associated Location entity.
    private String locationCode;
    private int temperature;
    private int humidity;
    private int precipation;
    private int windSpeed;
    private String status;
    private Date lastUpdated;
    @OneToOne
    @PrimaryKeyJoinColumn(name = "location_code", referencedColumnName = "code")
    @MapsId // Ensures shared PK-FK mapping
    private Location location;
}
