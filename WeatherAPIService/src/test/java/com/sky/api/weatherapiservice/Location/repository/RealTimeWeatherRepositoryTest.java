package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.Location;
import com.sky.api.weatherapicommon.entity.RealTimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
           real.setPrecipation(42);
           real.setStatus("snowy");
           real.setWindSpeed(12);
           real.setLastUpdated(new Date());
           RealTimeWeather updatedWeather=realTimeWeatherRepository.save(real);
       }
   }
}