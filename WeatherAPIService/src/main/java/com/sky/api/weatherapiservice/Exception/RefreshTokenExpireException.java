package com.sky.api.weatherapiservice.Exception;

public class RefreshTokenExpireException extends  RuntimeException{

    public RefreshTokenExpireException(String message, Throwable th)
    {
        super(message,th);
    }

    public RefreshTokenExpireException(String message) {
        super(message);
    }
}
