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
}
