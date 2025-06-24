package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.DailyWeather;
import com.sky.api.weatherapicommon.entity.DailyWeatherId;
import com.sky.api.weatherapicommon.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class DailyWeatherRepositoryTest {

   @Autowired DailyWeatherRepository dailyWeatherRepository;

   @Test
   public void testAdd(){
      String locationCode="NYC_USA";
      Location location=Location.builder().code(locationCode).build();
      DailyWeatherId id1 = new DailyWeatherId(16, 7, locationCode);
      DailyWeather dailyWeather1 = dailyWeatherRepository.findById(id1).orElse(null);
      if (dailyWeather1 == null) {
         dailyWeather1 = DailyWeather.builder()
                 .location(location)
                 .dailyWeatherId(id1)
                 .minTemp(28)
                 .maxTemp(36)
                 .precipitation(7)
                 .status("Sunny")
                 .build();
      }
      DailyWeather dbDailyWeather=dailyWeatherRepository.save(dailyWeather1);
      assertNotNull(dbDailyWeather);
      assertEquals(28,dbDailyWeather.getMinTemp());

   }

   @Test
   public void testDelete(){
      String locationCode="NYC_USA";
      DailyWeatherId id1 = new DailyWeatherId(16, 7, locationCode);
      dailyWeatherRepository.deleteById(id1);
   }

}