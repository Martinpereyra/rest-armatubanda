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

    @Query("SELECT m FROM Musician m INNER JOIN m.personalInformation pi INNER JOIN m.skillsInformation si INNER JOIN si.genres g WHERE m.isProfileSet = true AND (pi.name LIKE %?1 OR ?1 IS NULL) AND (g.name IN ?2 OR ?2 IS NULL)")
    List<Musician> findBy(String name,
                          List<String> genres,
                          String city,
                          List<String> instruments,
                          Experience exp,
                          Boolean look);
}
