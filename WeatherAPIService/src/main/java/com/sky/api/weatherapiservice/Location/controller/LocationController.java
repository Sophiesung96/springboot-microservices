package com.sky.api.weatherapiservice.Location.controller;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.Exception.LocationNotFoundException;
import com.sky.api.weatherapiservice.Location.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
public class LocationController {

    private LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Location>addLocation(@Valid @RequestBody Location location) {

        Location theLocation=locationService.add(location);
        URI uri=URI.create("v1/locations"+theLocation.getCode());
        return ResponseEntity.created(uri).body(theLocation);
    }

    @GetMapping
    public ResponseEntity<?> listAllLocations() {
        List<Location> list=locationService.list();
        if(list.isEmpty())
        {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getLocation(@PathVariable  String code) {
        Location location=locationService.get(code);
        if(location==null)
        {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        return ResponseEntity.ok().body(location);
    }


    @PutMapping
    public ResponseEntity<?> updateLocation(@RequestBody Location location) {
        try
        {
            Location updatedLocation=locationService.update(location);
            return ResponseEntity.ok().body(updatedLocation);
        }
        catch(LocationNotFoundException e)
        {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable String code)
    {
        try
        {
            locationService.deleteLocation(code);
            return ResponseEntity.noContent().build();
        }
        catch(LocationNotFoundException e)
        {
            return ResponseEntity.noContent().build();
        }
    }


}
