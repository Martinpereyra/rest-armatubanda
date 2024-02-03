package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.MusicianApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<MusicianApplication,Integer> {


    @Query("SELECT a FROM MusicianApplication a WHERE a.advertisement.id = :adId")
    List<MusicianApplication> findAllByAd(@Param("adId") int adId);

    @Query("SELECT COUNT(a) > 0 FROM MusicianApplication a WHERE a.musician.id = :musicianId AND a.advertisement.id = :adId")
    boolean existsByMusicianIdAndAdvertisementId(@Param("musicianId") int musicianId, @Param("adId") int adId);

}
