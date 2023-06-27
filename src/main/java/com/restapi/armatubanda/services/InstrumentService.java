package com.restapi.armatubanda.services;

import com.restapi.armatubanda.model.Instrument;
import com.restapi.armatubanda.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstrumentService {
    private final InstrumentRepository instrumentRepository;


    public ResponseEntity<Instrument> create(String instrumentName) {
        Instrument instrumentToSave = new Instrument();
        instrumentToSave.setName(instrumentName);
        return ResponseEntity.ok(instrumentRepository.save(instrumentToSave));
    }

    public Optional<Instrument> getInstrument(String name) {
        return instrumentRepository.findInstrumentByName(name);
    }
}
