package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.RefreshToken;
import com.sky.api.weatherapicommon.entity.User;
import com.sky.api.weatherapiservice.Exception.RefreshTokenExpireException;
import com.sky.api.weatherapiservice.Exception.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("production")
public class TokenService {
    @Value("${app.security.jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtility jwtUtility;
    private final PasswordEncoder encoder;

    public AuthResponse generateToken(User user){
        String accessToken=jwtUtility.generateAccessToken(user);
        String refreshToken= UUID.randomUUID().toString();
        long reExpiryDate=System.currentTimeMillis()+refreshTokenExpiration*60000;
        RefreshToken refreshT= RefreshToken
                .builder()
                .token(refreshToken)
                .user(user)
                .expiryTime(new Date(reExpiryDate))
                .build();

        refreshTokenRepository.save(refreshT);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request){
        String rawRefreshToken=request.getRefreshToken();
        List<RefreshToken> token=refreshTokenRepository.findByUsername(request.getUsername());
        RefreshToken foundRefreshToken=null;
        for(RefreshToken refresh:token)
        {
            if(encoder.matches(rawRefreshToken,refresh.getToken()))
            {
                foundRefreshToken=refresh;
            }
        }
        if(foundRefreshToken==null)
        {
            throw new RefreshTokenNotFoundException("The refresh token cannot be found");
        }

        Date currentDate=new Date();
        if(foundRefreshToken.getExpiryTime().before(currentDate) ){
            throw new RefreshTokenExpireException("Your refresh token has expired");
        }
        AuthResponse response=generateToken(foundRefreshToken.getUser());
        // delete the original refresh token stored in the db
        refreshTokenRepository.delete(foundRefreshToken);
         return response;
    }



}
