package com.sky.api.weatherapiservice.security.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotNull
    @Length(min = 5, max = 20)
    private String username;

    @NotNull @Length(min = 36, max = 50)
    private String refreshToken;
}
