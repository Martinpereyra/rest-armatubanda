package com.restapi.armatubanda.repository;


import com.restapi.armatubanda.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument,Integer> {


    Optional<Instrument> findInstrumentByName(String name);
}
