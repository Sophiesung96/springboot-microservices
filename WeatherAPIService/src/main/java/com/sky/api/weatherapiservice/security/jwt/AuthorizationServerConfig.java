package com.sky.api.weatherapiservice.security.jwt;


import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.sky.api.weatherapicommon.entity.ClientApp;
import com.sky.api.weatherapiservice.Location.repository.ClientAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Configuration
@Profile("production")
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final RsaKeyProperties keyPair;

    @Value("${weatherapi.security.jwt.issuer}")
    private String issuerName;
    @Value("${weatherapi.security.jwt.access-token.expiration}")
    private  int accessTokenExpirationTime;

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }


    @Bean
    JwtEncoder jwtEncoder(){
        // Build JWK from RSA key pair
        RSAKey rsaKey = new RSAKey.Builder(keyPair.publicKey())
                .privateKey(keyPair.privateKey())
                .build();
        // Wrap the key in a JWKSource
        JWKSource<SecurityContext>  jwkSource=new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(keyPair.publicKey()).build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http
                .securityMatcher(endpointsMatcher) // Use this to limit the filter chain to OAuth2 endpoints only
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .with(authorizationServerConfigurer, configurer -> {

                });

        return http.build();

    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){
        return context -> {
            if(OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()))
            {
                RegisteredClient client=context.getRegisteredClient();
                JwtClaimsSet.Builder builder=context.getClaims();
                builder.issuer(issuerName);
                builder.expiresAt(Instant.now().plus(accessTokenExpirationTime, ChronoUnit.MINUTES));
                builder.claims(claims->{
                    claims.put("scope",client.getScopes());
                    claims.put("name",client.getClientName());
                    claims.remove("aud");
                });
            }
        };
    }

    @Bean
    RegisteredClientRepository repository(ClientAppRepository repo)
    {
        return new RegisteredClientRepository() {
            @Override
            public void save(RegisteredClient registeredClient) {

            }

            @Override
            public RegisteredClient findById(String id) {
                return null;
            }

            @Override
            public RegisteredClient findByClientId(String clientId) {
                Optional<ClientApp> result=repo.findByCLientId(clientId);
                if(result.isEmpty()) return null;

                ClientApp clientApp=result.get();

                return RegisteredClient.withId(clientApp.getId().toString())
                        .clientName(clientApp.getName())
                        .clientSecret(clientApp.getClientSecret())
                        .clientId(clientApp.getClientId())
                        .scope(clientApp.getRole().toString())
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .build();
            }
        };
    }
}
