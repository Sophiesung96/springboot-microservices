package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends FilterableLocationRepository,JpaRepository<Location, String> {
    // Custom query methods (if any)

    @Query("select l from Location l where l.trashed=false")
    @Deprecated
    Page<Location> findUntrashed(Pageable pageable);

    @Query("select l from Location l where l.trashed = false and l.code = :code")
    Location findByCode(@Param("code") String code);

    @Query("update Location set trashed=true where code=:code")
    @Modifying
    void trashByCode(@Param("code") String code);

    @Query("select l from Location  l where l.countryCode=:countrycode and l.cityName=:cityname and l.trashed=false")
    Location findByCountryCodeCityName(@Param(value = "countrycode") String countryCode, @Param(value = "cityname")String cityName);


}
