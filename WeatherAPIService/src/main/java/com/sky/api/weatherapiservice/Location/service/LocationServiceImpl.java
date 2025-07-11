package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.DTO.LocationDTO;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import com.sky.api.weatherapiservice.mapper.WeatherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    @Override
    public Location add(Location location) {
        Location newLocation=locationRepository.save(location);
        if(newLocation==null)
        {
            throw new LocationNotFoundException();
        }
        return newLocation;
    }

    @Deprecated
    public List<Location> list() {
        return null;
        //return locationRepository.findUntrashed();
    }


    @Override
    public Page<Location> listByPage(int pageNum, int pageSize, Sort sort, Map<String, Object> filterFields) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return locationRepository.listWithFilters(pageable, filterFields);
    }


    @Override
    public Location get(String code) {
        return locationRepository.findByCode(code);
    }

    @Override
    public Location update(Location locationRequest) {

        String code=locationRequest.getCode();
        Location locationDb=get(code);
        if(locationDb==null)
        {
            throw new LocationNotFoundException(code);
        }
        locationDb.setCityName(locationRequest.getCityName());
        locationDb.setRegionName(locationRequest.getRegionName());
        locationDb.setCountryName(locationRequest.getCountryName());
        locationDb.setCountryCode(locationRequest.getCountryCode());
        locationDb.setEnabled(locationRequest.isEnabled());

        return locationRepository.save(locationDb);
    }

    @Override
    public void deleteLocation(String code) {
        Location locationDb=get(code);
        if(locationDb==null)
        {
            throw new LocationNotFoundException(code);
        }
        //labeling this code as being trashed without directly deleting it from the database
        locationRepository.trashByCode(code);
    }

    @Override
    public Location findByCountryCodeCityName(String countryCode, String cityName) {

        Location location= locationRepository.findByCountryCodeCityName(countryCode, cityName);
        if(location==null)
        {
            throw new LocationNotFoundException(countryCode,cityName);
        }
        return location;
    }

    @Override
    public Location findByLocationCode(String locationCode) {
        return locationRepository.findByCode(locationCode);
    }


}
