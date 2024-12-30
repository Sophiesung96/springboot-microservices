package com.sky.api.weatherapiservice.Exception;

public class LocationNotFoundException extends  RuntimeException{

    public LocationNotFoundException() {
        super();
    }

    public LocationNotFoundException(String message) {
        super("No location found with the given code"+ message);
    }

    public LocationNotFoundException(String countryCode, String city) {
        super("No location found with the given country code: "+ countryCode+" and city name: "+ city);
    }
}
