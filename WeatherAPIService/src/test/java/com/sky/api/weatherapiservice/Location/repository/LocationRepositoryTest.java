package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private DailyWeatherRepository dailyWeatherRepository;

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
    public void testListFirstPage(){
        int pageSize=5;
        int pageNum=0;
        Pageable page= PageRequest.of(pageNum,pageSize);
        Page<Location> location=locationRepository.findUntrashed(page);
        assertEquals(location.getSize(),pageSize);
    }

    @Test
    public void testList2ndPageWithSort(){
        int pageSize=5;
        int pageNum=0;
        Sort sort=Sort.by("code").descending();
        Pageable page= PageRequest.of(pageNum,pageSize,sort);
        Page<Location> location=locationRepository.findUntrashed(page);
        assertEquals(location.getSize(),pageSize);
        location.forEach(System.out::println);
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

    @Test
    @Transactional
    public void testAddDailyWeatherData() {
        Location location = locationRepository.findByCode("DELHL_IN");

        if (location == null) {
            throw new RuntimeException("Location with code DELHL_IN not found!");
        }

        List<DailyWeather> dailyWeatherList = location.getListDailyWeather();

        DailyWeatherId id1 = new DailyWeatherId(16, 7, "DELHL_IN");
        DailyWeatherId id2 = new DailyWeatherId(17, 7, "DELHL_IN");

        // Fetch existing entities before creating new ones
        DailyWeather dailyWeather1 = dailyWeatherRepository.findById(id1).orElse(null);
        if (dailyWeather1 == null) {
            dailyWeather1 = DailyWeather.builder()
                    .location(location)
                    .dailyWeatherId(id1)
                    .minTemp(25)
                    .maxTemp(33)
                    .precipitation(20)
                    .status("Sunny")
                    .build();
        }

        DailyWeather dailyWeather2 = dailyWeatherRepository.findById(id2).orElse(null);
        if (dailyWeather2 == null) {
            dailyWeather2 = DailyWeather.builder()
                    .location(location)
                    .dailyWeatherId(id2)
                    .minTemp(26)
                    .maxTemp(38)
                    .precipitation(44)
                    .status("Humid")
                    .build();
        }

        dailyWeatherList.add(dailyWeather1);
        dailyWeatherList.add(dailyWeather2);

        location.setListDailyWeather(dailyWeatherList);

        Location updatedLocation = locationRepository.save(location);
        assertTrue(updatedLocation.getListDailyWeather().contains(dailyWeather1));
    }








}