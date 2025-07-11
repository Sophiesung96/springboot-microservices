package com.sky.api.weatherapiservice.Exception;

public class JwtValidationException extends  Exception{
    public JwtValidationException(String message,Throwable cause){
        super(message,cause);
    }
}
