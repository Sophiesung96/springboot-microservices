package com.sky.api.weatherapiservice.Location.service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapiservice.Exception.GeoLocationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class GeoLocationService {

    private String DB_PATH="/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private IP2Location ip2Location;

    public GeoLocationService() {
        try
        {
            ip2Location = new IP2Location();
            InputStream inputStream = getClass().getResourceAsStream(DB_PATH);
            byte[] data=inputStream.readAllBytes();
            ip2Location.Open(DB_PATH);
            inputStream.close();
        }
        catch (IOException e) {
            log.error(e.getMessage(),e);
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
