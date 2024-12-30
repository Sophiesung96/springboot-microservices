package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.Location;

import java.util.List;

public interface LocationService {

     Location add(Location location);

    List<Location> list();

    Location get(String code);

    Location update(Location location);

    void deleteLocation(String code);
    Location findByCountryCodeCityName(String countryCode, String cityName);
    Location findByLocationCode(String locationCode);

}
