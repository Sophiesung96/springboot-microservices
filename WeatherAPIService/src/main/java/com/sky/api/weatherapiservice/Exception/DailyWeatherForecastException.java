package com.sky.api.weatherapiservice.Exception;

public class DailyWeatherForecastException extends  RuntimeException{
    public DailyWeatherForecastException() {
        super();
    }

    public DailyWeatherForecastException(String message) {
        super(message);
    }

    public DailyWeatherForecastException(String message, Throwable cause) {
        super(message, cause);
    }
}
