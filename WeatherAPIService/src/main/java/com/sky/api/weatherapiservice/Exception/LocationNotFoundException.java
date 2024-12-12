package com.sky.api.weatherapiservice.Exception;

public class LocationNotFoundException extends  RuntimeException{

    public LocationNotFoundException() {
        super();
    }

    public LocationNotFoundException(String message) {
        super(message);
    }
}
