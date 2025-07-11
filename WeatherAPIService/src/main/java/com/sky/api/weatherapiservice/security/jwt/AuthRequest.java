package com.sky.api.weatherapiservice.security.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthRequest {
    @NotNull
    @Length(min = 1, max = 20)
    private String username;
    @NotNull
    @Length(min = 1, max = 10)
    private String password;
}
