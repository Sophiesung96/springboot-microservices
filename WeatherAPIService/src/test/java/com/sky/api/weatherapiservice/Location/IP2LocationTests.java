package com.sky.api.weatherapiservice.Location;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IP2LocationTests {

    private String DB_PATH="C:\\Users\\sunny\\IdeaProjects\\WeatherProjectAPI\\WeatherAPIService\\src\\main\\resources\\ip2locdb\\IP2LOCATION-LITE-DB3.BIN";
    @Test
    public void testInvalidIP() throws IOException {
        IP2Location ip2Location=new IP2Location();
        ip2Location.Open(DB_PATH);
        String ipAddress="abc";
        IPResult ipResult=ip2Location.IPQuery(ipAddress);
        assertEquals(true,ipResult.getStatus().equals("INVALID_IP_ADDRESS"));
    }

    @Test
    public void testValidIP2() throws IOException {
        IP2Location ip2Location=new IP2Location();
        ip2Location.Open(DB_PATH);
        String ipAddress="164.100.161.98";
        IPResult ipResult=ip2Location.IPQuery(ipAddress);
        System.out.println(ipResult);
        assertEquals(true,ipResult.getStatus().equals("OK"));
    }



}
