package com.restapi.armatubanda.services;

import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicianService {
    private final MusicianRepository musicianRepository;

    public Optional<Musician> getMusician(String username){
        return musicianRepository.findByEmail(username);
    }

    public void save(Musician musician){
        musicianRepository.save(musician);
    }



}
