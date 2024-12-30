package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.HourlyWeather;
import com.sky.api.weatherapicommon.entity.HourlyWeatherId;
import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

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
        realTimeWeather.setPrecipitation(40);
        realTimeWeather.setStatus("snowy");
        realTimeWeather.setWindSpeed(15);
        realTimeWeather.setLastUpdated(new Date());
        Location updatedLocation=locationRepository.save(location);
    }


    @Test
    public void testAddSuccess(){
        Location location= Location.builder()
                .countryName("India")
                .cityName("Mumbai")
                .countryCode("IN")
                .regionName("Maharashtra")
                .enabled(true)
                .code("MBMH_IN")
                .build();
        Location savedLocation=locationRepository.save(location);
        assertNotNull(savedLocation);
    }

    @Test
    public void testAddHourlyWeatherData() {
        // Step 1: Fetch the location
        Location location = locationRepository.findByCode("DELHL_IN");


        // Step 2: Create HourlyWeatherId and HourlyWeather entries
        HourlyWeatherId forecastId1 = new HourlyWeatherId();
        forecastId1.setHourOfDay(10);
        forecastId1.setLocationCode(location.getCode());

        HourlyWeather forecast1 = HourlyWeather.builder()
                .id(forecastId1)
                .temperature(15)
                .precipitation(40)
                .build();
        forecast1.setLocation(location); // Link to parent location

        HourlyWeatherId forecastId2 = new HourlyWeatherId();
        forecastId2.setHourOfDay(11);
        forecastId2.setLocationCode(location.getCode());

        HourlyWeather forecast2 = HourlyWeather.builder()
                .id(forecastId2)
                .temperature(16)
                .precipitation(50)
                .build();
        forecast2.setLocation(location); // Link to parent location

        // Step 3: Add HourlyWeather entries to the location
        location.getListHourlyWeather().add(forecast1);
        location.getListHourlyWeather().add(forecast2);

        // Step 4: Save the location
        Location updatedLocation = locationRepository.save(location);

        // Step 5: Validate that the data is persisted
        assertFalse(updatedLocation.getListHourlyWeather().isEmpty());
    }

    @Test
    public void testFindByCountryCodeAndCityNotFound(){
        String countryCode="Test";
        String cityName="Test";
        Location location=locationRepository.findByCountryCodeCityName(countryCode,cityName);
        assertTrue(location==null);
    }

    @Test
    public void testFindByCountryCodeAndCityFound(){
        String countryCode="IN";
        String cityName="Delhi";
        Location location=locationRepository.findByCountryCodeCityName(countryCode,cityName);
        assertEquals(location.getCountryCode(),countryCode);
        assertEquals(location.getCityName(),cityName);
    }

    @Test
    public void testFindByCodeFound(){
        String locationCode="LDN_UK";
        Location location=locationRepository.findByCode(locationCode);
        assertNotNull(location);
    }







}