package com.sky.api.weatherapiservice.Location.service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class GeoLocationService {

    private String DB_PATH="classpath:/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private IP2Location ip2Location;
    private final ResourceLoader resourceLoader;

    public GeoLocationService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.ip2Location = new IP2Location();
        try
        {
            Resource resource=resourceLoader.getResource(DB_PATH);
            InputStream inputStream = resource.getInputStream();
            if (inputStream == null) {
                throw new IOException("Resource not found: " + DB_PATH);
            }
            ip2Location.Open(resource.getFile().getAbsolutePath());
            inputStream.close();
        }
        catch (IOException e) {
            log.error("Failed to load GeoLocation database: {}", e.getMessage());
            throw new GeoLocationException("Could not load IP2Location database", e);
        }
    }

    public Location getLocation(String ipAddress){
        try
        {
            IPResult ipResult= ip2Location.IPQuery(ipAddress);
            if(!ipResult.getStatus().equals("OK"))
            {
                throw new GeoLocationException("GeoLocation failed with status "+ipResult.getStatus());
            }
            log.info("return ip value:{}",ipResult.toString());
            return new Location(ipResult.getCity(),ipResult.getRegion(),ipResult.getCountryLong(),ipResult.getCountryShort());
        }catch(IOException e)
        {
            throw new GeoLocationException("Error querying database",e);
        }

    }

}
