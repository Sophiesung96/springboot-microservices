package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.User;
import com.sky.api.weatherapiservice.Exception.JwtValidationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private  JwtUtility jwtUtility;
    @Qualifier("handlerExceptionResolver")
    private  HandlerExceptionResolver handlerResolver;

    public JwtTokenFilter(
            JwtUtility jwtUtility,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerResolver
    ) {
        this.jwtUtility = jwtUtility;
        this.handlerResolver = handlerResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!hasAuthorizationBearer(request))
        {
            filterChain.doFilter(request,response);
            return;
        }
        String token=getBearerToken(request);
        log.info("token: {}",token);
        try {
            Claims claims=jwtUtility.validAccessToken(token);
            UserDetails userDetails=getUserDetails(claims);
            setAuthenticationContext(userDetails,request);
            filterChain.doFilter(request,response);
            clearAuthenticationContext();
        } catch (JwtValidationException e) {
            log.error(e.getMessage(),e);
            handlerResolver.resolveException(request,response,null,e);
            return;
        }
    }

    private void clearAuthenticationContext() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        var authentication=new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    private UserDetails getUserDetails(Claims claims) {
        String subject= (String) claims.get(Claims.SUBJECT);
        String [] subjects=subject.split(",");
        Integer userId=Integer.valueOf(subjects[0]);
        String userName=subjects[1];
        String role= (String) claims.get("role");
        User user= User.builder()
                .username(userName)
                .role(role)
                .id(userId).build();
        log.info("User parsed from JWT: {}, {}, {}",user.getId(), user.getUsername(), user.getRole());
        return new CustomerUserDetail(user);
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header=request.getHeader("Authorization");
        log.info("Authorization Header: {}",header);
        if(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer"))
        {
            return false;
        }
        return true;
    }

    private String getBearerToken(HttpServletRequest request)
    {
        String header=request.getHeader("Authorization");
        String [] array=header.split(" ");
        if(array.length==2) return array[1];
        return null;
    }
}
