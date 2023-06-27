package com.restapi.armatubanda.repository;


import com.restapi.armatubanda.model.Instrument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstrumentRepository extends CrudRepository<Instrument,Integer> {


    Optional<Instrument> findInstrumentByName(String name);
}
