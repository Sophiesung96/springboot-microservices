package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import java.util.Map;

public interface FilterableLocationRepository {

    public Page<Location> listWithFilters(Pageable pageable, Map<String, Object> filterFields);
}
