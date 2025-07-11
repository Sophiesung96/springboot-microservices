package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public interface LocationService {

    Location add(Location location);

    @Deprecated
    List<Location> list();

    Page<Location> listByPage(int pageNum, int pageSize, Sort sort, Map<String,Object> filterFields);

    Location get(String code);

    Location update(Location location);

    void deleteLocation(String code);
    Location findByCountryCodeCityName(String countryCode, String cityName);
    Location findByLocationCode(String locationCode);

}
