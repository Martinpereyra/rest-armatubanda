package com.restapi.armatubanda.repository;


import com.restapi.armatubanda.model.BandAdvertisement;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<BandAdvertisement,Integer> {
    List<BandAdvertisement> findAllByBandId(int bandId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BandAdvertisement ba WHERE ba.band.id = :bandId")
    void deleteByBandId(@Param("bandId") int bandId);
}
