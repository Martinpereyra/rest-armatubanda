package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.Experience;
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
            "JOIN m.preferenceInformation pi "+
            "JOIN m.skillsInformation si " +
            "JOIN si.genres s "+
            "JOIN si.instrumentExperience ie "+
            "JOIN Instrument i on ie.instrument.id = i.id "+
            "WHERE m.isProfileSet = true AND " +
            "((c.name LIKE %:name%) OR (:name IS NULL)) AND " +
            "(c.city LIKE %:city% OR (:city IS NULL)) AND " +
            "(s.name in :genres OR (:genres IS NULL)) AND "+
            "(i.name in :instruments OR (:instruments IS NULL)) " +
            "  AND (si.generalExperience = :experience OR (:experience IS NULL))" +
            " AND (pi.lookingBands = :look OR (:look IS NULL))"
    )
    List<Musician> findBy(@Param("name")String name,@Param("city")String city,@Param("genres")List<String> genres,@Param("instruments")List<String> instruments,@Param("experience") Experience exp,@Param("look") Boolean look);
}
