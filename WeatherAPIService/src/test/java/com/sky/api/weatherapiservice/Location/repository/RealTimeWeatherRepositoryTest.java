package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class RealTimeWeatherRepositoryTest {
   @Autowired
    private RealTimeWeatherRepository realTimeWeatherRepository;

   @Test
   public void testUpdate(){
       String locationCode="NYC_USA";
       Optional<RealTimeWeather> realTimeWeather=realTimeWeatherRepository.findById(locationCode);
       if(realTimeWeather.isPresent())
       {
           RealTimeWeather real=realTimeWeather.get();
           real.setTemperature(-2);
           real.setHumidity(32);
           real.setPrecipitation(42);
           real.setStatus("snowy");
           real.setWindSpeed(12);
           real.setLastUpdated(new Date());
           RealTimeWeather updatedWeather=realTimeWeatherRepository.save(real);
       }
   }

    @Test
   public void testFindByCountryCodeAndCityNotFound(){

       String countryCOde="JP";
       String cityName="Tokyo";
       RealTimeWeather realTimeWeather=realTimeWeatherRepository.findByCountryCodeAndCity(countryCOde,cityName);
       assertNull(realTimeWeather);

   }


    @Test
    public void testFindByCountryCodeAndCityFound(){

        String countryCOde="US";
        String cityName="New York City";
        RealTimeWeather realTimeWeather=realTimeWeatherRepository.findByCountryCodeAndCity(countryCOde,cityName);
        assertNotNull(realTimeWeather);
        assertEquals(cityName,realTimeWeather.getLocation().getCityName());
    }

}