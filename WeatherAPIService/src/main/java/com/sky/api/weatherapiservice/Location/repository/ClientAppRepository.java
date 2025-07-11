package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.ClientApp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ClientAppRepository extends CrudRepository<ClientApp, Integer> {

    @Query("SELECT c FROM ClientApp c where c.clientId=:clientId")
    public Optional<ClientApp> findByCLientId(@Param("clientId") String clientId);
}
