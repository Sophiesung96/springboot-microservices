package com.sky.api.weatherapiservice.Location.service;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    @Override
    public Location add(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public List<Location> list() {
        return locationRepository.findUntrashed();
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
            throw new LocationNotFoundException("No location found with the given code");
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
            throw new LocationNotFoundException("No Location was found with this code: "+code);
        }
        //labeling this code as being trashed without directly deleting it from the database
        locationRepository.trashByCode(code);
    }


}
