package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void testGetNoFound(){
        String code="ABCD";
        Location location=locationRepository.findByCode(code);
        assertNull(location);
    }

    @Test
    public void testGetFound(){
        String code="LDN_UK";
        Location location=locationRepository.findByCode(code);
        assertNotNull(location);
    }

    @Test
    public void testTrashSuccess(){
        String code="LDN_UK";
        locationRepository.trashByCode(code);
        Location location=locationRepository.findByCode(code);

    }

    @Test
    public void testAddRealTimeWeatherData(){
        String locationCode="NYC_USA";
        Location location=locationRepository.findByCode(locationCode);
        RealTimeWeather realTimeWeather=location.getRealTimeWeather();
        if(realTimeWeather==null)
        {
            realTimeWeather=new RealTimeWeather();
            realTimeWeather.setLocation(location);
            location.setRealTimeWeather(realTimeWeather);
        }
        realTimeWeather.setTemperature(-1);
        realTimeWeather.setHumidity(30);
        realTimeWeather.setPrecipation(40);
        realTimeWeather.setStatus("snowy");
        realTimeWeather.setWindSpeed(15);
        realTimeWeather.setLastUpdated(new Date());
        Location updatedLocation=locationRepository.save(location);
    }

}