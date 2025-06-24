package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.Location;

public interface FullWeatherService {

    Location getByLocation(Location locationFromIP);

    Location get(String locationCode);

    Location update(String locationCode, Location locationInRequest);
}
