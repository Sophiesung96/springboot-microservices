package com.sky.api.weatherapiservice.Filter;

import com.sky.api.weatherapicommon.entity.RealTimeWeather;

public class RealtimeWeatherFieldFilter {

    public boolean equals(Object object){
        if(object instanceof RealTimeWeather){
            RealTimeWeather weather= (RealTimeWeather) object;
            return weather.getStatus()==null;
        }
        return false;
    }
}
