package com.sky.api.weatherapiservice.security.jwt;

import com.sky.api.weatherapicommon.entity.RefreshToken;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.sky.api.weathercommon.entity")
@Rollback(value = false)
public class RefreshTokenRepositoryTest {
    @Autowired
    RefreshTokenRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    TestEntityManager manager;
    @Autowired
    private TestEntityManager testEntityManager;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

    }

    @Test
    public void testFindByUsernameNotFound(){
        String usernameNotExist="abcdef";
        List<RefreshToken> token=repo.findByUsername(usernameNotExist);
        assertThat(token).isEmpty();
    }

    @Test
    public void testDeleteByExpiryTest(){
        String jpql="select count(rt) from RefreshToken rt where rt.expiryTime<=CURRENT_TIME";
        Query query= testEntityManager.getEntityManager().createQuery(jpql);
        Long numberOfExpiredRefreshTokens= (Long) query.getSingleResult();
        int rowDeleted=repo.deleteByExpiryTime();
        assertEquals(numberOfExpiredRefreshTokens,rowDeleted);
    }
}
