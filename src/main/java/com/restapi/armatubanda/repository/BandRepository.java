package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BandRepository extends JpaRepository<Band,Integer> {
}
