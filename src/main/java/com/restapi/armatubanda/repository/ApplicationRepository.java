package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.MusicianApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<MusicianApplication,Integer> {


    @Query("SELECT a FROM MusicianApplication a WHERE a.advertisement.id = :adId")
    List<MusicianApplication> findAllByAd(@Param("adId") int adId);

    @Query("SELECT COUNT(a) > 0 FROM MusicianApplication a WHERE a.musician.id = :musicianId AND a.advertisement.id = :adId")
    boolean existsByMusicianIdAndAdvertisementId(@Param("musicianId") int musicianId, @Param("adId") int adId);


    @Modifying
    @Transactional
    @Query("DELETE FROM MusicianApplication ma WHERE ma.musician.id = :musicianId AND ma.advertisement.id IN (SELECT ad.id FROM BandAdvertisement  ad WHERE ad.band.id = :bandId) ")
    void deletePendingApplications(@Param("bandId") int bandId, @Param("musicianId") int musicianId);

}
