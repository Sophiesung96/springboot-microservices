package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.ClientApp;
import com.sky.api.weatherapicommon.entity.User;
import com.sky.api.weatherapiservice.Location.repository.ClientAppRepository;
import com.sky.api.weatherapiservice.Location.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.sky.api.weatherapicommon.entity.AppRole.SYSTEM;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityOauth2Test {

    @Autowired
    private ClientAppRepository clientAppRepository;

    @Autowired
    private UserRepository userRepository;



    @Autowired
    MockMvc mockMvc;
    private static final String GET_ACCESS_TOKEN_ENDPOINT="/oauth2/token";

    @Test
    public void testGetAuthorizationServerAccessTokenFail() throws Exception {
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .param("client_id","abc")
                        .param("client_secret","abc")
                        .param("grant_type","client_credentials"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error",is("invalid client")));
    }

    @Test
    public void testGetAuthorizationServerAccessTokenSuccess () throws Exception {
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .param("client_id","47APZ0tCfnpodKPt4mvs")
                        .param("client_secret","abc")
                        .param("grant_type","client_credentials"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type",is("Bearer")));
    }

    @Test
    public void testAddAdminUser() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "abc";
        String newPassword = encoder.encode(rawPassword);

        User user = User.builder()
                .username("Sophie Sung")
                .password(newPassword)
                .role("ADMIN")
                .build();

        User savedUser = userRepository.save(user);

        String clientId = RandomStringUtils.randomAlphanumeric(20);

        ClientApp app = ClientApp.builder()
                .clientId(clientId)
                .name("abc")
                .clientSecret(newPassword)
                .user(savedUser) // use saved user
                .enabled(true)
                .role(SYSTEM)
                .trashed(false)
                .build();

        clientAppRepository.save(app);
    }

}
