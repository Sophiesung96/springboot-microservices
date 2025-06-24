package com.sky.api.weatherapiservice.Utility;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.sky.api.weatherapicommon.entity.Location;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtility {

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        log.info("getIpAddress:{} " , ip);
        return ip;
    }



}
