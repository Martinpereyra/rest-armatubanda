package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.Invitation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation,Integer> {

    @Modifying
    @Query("DELETE FROM Invitation i where i.bandInvitation.id = :bandId")
    void deleteAllByBandId(@Param("bandId") int bandId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Invitation i where i.bandInvitation.id = :bandId and i.musicianInvited.id = :musicianId ")
    void deleteByBandIdAndMusicianId(@Param("bandId") int bandId, @Param("musicianId") int musicianId);

    @Query("SELECT i FROM Invitation i WHERE i.musicianInvited.id = :musicianId AND i.bandInvitation.id = :bandId")
    Optional<Invitation> findByMusicianInvitedIdAndBandId(@Param("musicianId") int musicianId, @Param("bandId") int bandId);
}
