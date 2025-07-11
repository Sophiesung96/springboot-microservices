package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.User;
import com.sky.api.weatherapiservice.Exception.JwtValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtUtility {
  private static final String SECRET_KEY_ALGORITHM="HmacSHA512";
  private String issuerName;
  private String secretKey;
  private int accessTokenExpiration;

  public String generateAccessToken(User user){
      if(user==null || user.getId()==null || user.getUsername()==null
              || user.getRole()==null){
          throw new IllegalArgumentException("user object is null or its field have null values");
      }
      long expirationTimeInMillis=System.currentTimeMillis()+accessTokenExpiration*60000;
      String subject=String.format("%s,%s",user.getId(),user.getUsername());
      return Jwts.builder()
              .subject(subject)
              .issuer(issuerName)
              .issuedAt(new Date())
              .expiration(new Date(expirationTimeInMillis))
              .claim("role",user.getRole())
              .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), Jwts.SIG.HS512)
              .compact();
  }

  public Claims validAccessToken(String token) throws JwtValidationException {
      try{
          SecretKeySpec spec=new SecretKeySpec(secretKey.getBytes(),SECRET_KEY_ALGORITHM);
          return Jwts.parser()
                  .verifyWith(spec)
                  .build()
                  .parseSignedClaims(token)
                  .getPayload();
      }catch(ExpiredJwtException ex)
      {
          throw new JwtValidationException("Access token is expired",ex);
      }catch(IllegalArgumentException ex)
      {
          throw new JwtValidationException("Access token is illegal",ex);
      }
      catch(MalformedJwtException ex)
      {
          throw new JwtValidationException("Access token is malformed",ex);
      }
      catch(UnsupportedJwtException ex)
      {
          throw new JwtValidationException("Access token is unsupported",ex);
      }
  }
}
