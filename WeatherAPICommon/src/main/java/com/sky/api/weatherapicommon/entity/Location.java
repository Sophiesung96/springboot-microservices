package com.sky.api.weatherapicommon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @Column(length = 12, name = "code", nullable = false, unique = true)
    @NotNull(message = "The code should not be left blank!")
    @Length(min=3,max=12,message = "Location code must have 3-12 characters")
    private String code;

    @Column(length = 128, name = "city_name", nullable = false)
    @Length(min=6,max=128,message = "Location code must have 3-12 characters")
    @NotNull(message = "The city name should not be left blank!")
    private String cityName;

    @Column(length = 128, name = "region_name", nullable = false)
    @NotNull(message = "The region name should not be left blank!")
    private String regionName;

    @Column(length=64,name = "country_name", nullable = false)
    @NotNull(message = "The country name should not be left blank!")
    private String countryName;

    @Column(length = 2, name = "country_code", nullable = false)
    @NotNull(message = "The country code should not be left blank!")
    private String countryCode;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "trashed")
    private boolean trashed;

    @OneToOne(mappedBy = "location",cascade = CascadeType.ALL)
    private RealTimeWeather realTimeWeather;

}
