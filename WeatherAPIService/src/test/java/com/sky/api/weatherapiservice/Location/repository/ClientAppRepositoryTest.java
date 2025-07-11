package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.ClientApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class ClientAppRepositoryTest {

    @Autowired
    ClientAppRepository clientAppRepository;

    @Test
    public void testByClientIdNotFound(){
        String clientId="xxx";
        Optional<ClientApp> client=clientAppRepository.findByCLientId(clientId);
        assertFalse(client.isPresent());
    }

    @Test
    public void testByClientIdFound(){

    }
}