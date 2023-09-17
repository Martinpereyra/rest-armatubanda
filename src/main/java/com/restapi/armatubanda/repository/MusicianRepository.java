package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.Musician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicianRepository extends JpaRepository<Musician, Integer> {

    Optional<Musician> findByEmail(String email);

    @Query("SELECT DISTINCT m FROM Musician m " +
            "LEFT JOIN m.instruments i " +
            "JOIN m.musicianContactInformation c " +
            "WHERE m.isProfileSet = true AND " +
            " c.name LIKE %?1% OR " +
            "c.city LIKE %?2% OR " +
            "i.name IN ?3")
    List<Musician> findBy(String name, String city, List<String> instruments);
}
