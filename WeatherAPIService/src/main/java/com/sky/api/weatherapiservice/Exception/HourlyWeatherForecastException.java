package com.sky.api.weatherapiservice.Exception;

public class HourlyWeatherForecastException extends  RuntimeException{

    public HourlyWeatherForecastException() {
        super();
    }

    public HourlyWeatherForecastException(String message) {
        super(message);
    }

    public HourlyWeatherForecastException(String message, Throwable cause) {
        super(message, cause);
    }
}
