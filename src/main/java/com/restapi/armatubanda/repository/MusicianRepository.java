package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.model.Musician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicianRepository extends JpaRepository<Musician, Integer> {

    Optional<Musician> findByEmail(String email);

    @Query("SELECT DISTINCT m FROM Musician m " +
            "JOIN m.personalInformation c " +
            "JOIN m.skillsInformation.genres s "+
            "JOIN m.skillsInformation.instrumentExperience ie "+
            "JOIN Instrument i on ie.instrument.id = i.id "+
            "WHERE m.isProfileSet = true AND " +
            "((c.name LIKE %?1%) OR (?1 IS NULL)) AND " +
            "(c.city LIKE %?2% OR (?2 IS NULL)) AND " +
            "(s.name in :#{#genres} OR (?3 IS NULL)) AND "+
            "(i.name in :#{#instruments} OR (?4 IS NULL))")
    List<Musician> findBy(String name,String city,List<String> genres,List<String> instruments);
}
