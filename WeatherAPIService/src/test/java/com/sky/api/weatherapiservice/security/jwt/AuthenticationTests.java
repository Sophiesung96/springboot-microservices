package com.sky.api.weatherapiservice.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class AuthenticationTests {
    @Autowired
    AuthenticationManager manager;

    @Test
    public void testAuthenticationFail(){
        assertThrows( BadCredentialsException.class,()->{
            manager.authenticate(new UsernamePasswordAuthenticationToken("sophie","sung"));
        });
    }

    @Test
    public void testAuthenticationSuccess(){
        String username="admin";
        String password="admin";
        Authentication authentication=manager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        assertTrue(authentication.isAuthenticated());
        CustomerUserDetail detail= (CustomerUserDetail) authentication.getPrincipal();
        assertEquals(username,detail.getUsername());

    }
}
