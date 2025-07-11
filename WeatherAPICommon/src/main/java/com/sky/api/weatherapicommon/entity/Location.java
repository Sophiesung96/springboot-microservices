package com.sky.api.weatherapicommon.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Location {


    public Location(String code,String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public Location(String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

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
    @JsonManagedReference  //marks the owning side (parent).
    private RealTimeWeather realTimeWeather;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HourlyWeather> listHourlyWeather=new ArrayList<>();

    @OneToMany(mappedBy = "location",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DailyWeather> listDailyWeather=new ArrayList<>();
    @Override
    public String toString() {
        return
                code+" =>  cityName='" + cityName + '\'' + ", countryCode='" + countryCode + '\'' +
                        '\'' +   "code="+code+
                ", regionName='" + regionName + '\'' +
                ", countryName='" + countryName;

    }
}
