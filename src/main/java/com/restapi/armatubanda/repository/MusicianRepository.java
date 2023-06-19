package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.Musician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicianRepository extends JpaRepository<Musician, Integer> {

    Musician findByEmail(String email);
}
