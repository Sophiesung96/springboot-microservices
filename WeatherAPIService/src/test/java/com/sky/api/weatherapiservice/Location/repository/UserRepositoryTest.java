package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder encoder=new BCryptPasswordEncoder();

    @Test
    public void testAddFirstUser(){
        User user=User.builder()
                .username("admin")
                .role("write")
                .build();
        String rawPassword="admin";
        String encodedPass=encoder.encode(rawPassword);
        user.setPassword(encodedPass);
        User userDB=userRepository.save(user);
        assertNotNull(userDB);
    }

    @Test
    public void testFindUserNotFound(){
        Optional<User> user=userRepository.findByUsername("xxxxx");
        assertTrue(user.equals(Optional.empty()));
    }
}