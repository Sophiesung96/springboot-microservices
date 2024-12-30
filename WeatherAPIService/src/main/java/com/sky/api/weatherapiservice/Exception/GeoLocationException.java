package com.sky.api.weatherapiservice.Exception;

public class GeoLocationException extends RuntimeException {

    public GeoLocationException() {
        super();
    }

    public GeoLocationException(String message) {
        super(message);
    }

    public GeoLocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
