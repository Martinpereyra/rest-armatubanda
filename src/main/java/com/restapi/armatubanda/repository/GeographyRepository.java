package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.Geography;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographyRepository extends JpaRepository<Geography,Integer> {

    @Query("SELECT DISTINCT g.countryName FROM Geography g")
    List<String> findAllCountries();

    @Query("SELECT DISTINCT g.cityName FROM Geography g WHERE g.countryName LIKE %:countryName%")
    List<String> findAllCitiesByCountry(@Param("countryName") String countryName);
}
