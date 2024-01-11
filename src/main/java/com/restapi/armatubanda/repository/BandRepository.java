package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.dto.MusicianBandsDto;
import com.restapi.armatubanda.model.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BandRepository extends JpaRepository<Band,Integer> {
    List<Band> findAlLByMembers_Id(int musicianId);

    List<Band> findByMusicianLeaderId(int leaderId);
}
