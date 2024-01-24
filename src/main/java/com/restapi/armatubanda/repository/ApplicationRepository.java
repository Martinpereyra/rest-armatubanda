package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.MusicianApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<MusicianApplication,Integer> {


    @Query("SELECT a FROM MusicianApplication a WHERE a.bandAdvertisement.band.id = :bandId")
    List<MusicianApplication> findAllByBand(@Param("bandId") int bandId);
}
