package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.model.Instrument;
import com.restapi.armatubanda.services.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/instrument")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://utn-armatubanda.netlify.app")
public class InstrumentController{
    private final InstrumentService instrumentService;

    @PostMapping("{name}")
    public ResponseEntity<Instrument> create(@PathVariable("name") String instrumentName){
        return instrumentService.create(instrumentName);
    }

    @GetMapping()
    public List<Instrument> getAll() {
        return instrumentService.getAll();
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable("id") int id) {
        return instrumentService.delete(id);
    }

}
