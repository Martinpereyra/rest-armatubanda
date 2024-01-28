package com.restapi.armatubanda.repository;


import com.restapi.armatubanda.model.BandAdvertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<BandAdvertisement,Integer> {
    List<BandAdvertisement> findAllByBandId(int bandId);
}
