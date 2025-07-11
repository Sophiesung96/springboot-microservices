package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.User;
import com.sky.api.weatherapiservice.Exception.JwtValidationException;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilityTest {
    private static JwtUtility jwt;

    @BeforeAll
    static void setup(){
        jwt=new JwtUtility();
        jwt.setIssuerName("My Company");
        jwt.setAccessTokenExpiration(2);
        jwt.setSecretKey("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnoqrstuv+9-@$%#&%");
    }

    @Test
    public void testGeneralFail(){

    }

    @Test
    public void testGeneralSuccess(){
        User user= User.builder()
                .id(2)
                .username("admin")
                .role("write")
                .build();
        String token=jwt.generateAccessToken(user);
        System.out.println(token);
    }

    @Test
    public void testValidateFail(){

    }

    @Test
    public void testValidateSuccess(){
     String token="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxLHNvcGhpZSIsImlzcyI6Ik15IENvbXBhbnkiLCJpYXQiOjE3NTE2Mjk1ODAsImV4cCI6MTc1MTYyOTcwMCwicm9sZSI6InJlYWQifQ.epLZBa1EFSjTDqoqMiKSwQbxWQIWDeJqOcAHaHtvaaSON3wsaHY7C_kFzWwGX4dRF9BWREwZk8VwqIrLXXCcKA";
    assertDoesNotThrow(()->jwt.validAccessToken(token));
    }
}