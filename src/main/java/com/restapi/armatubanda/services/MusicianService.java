package com.restapi.armatubanda.services;

import com.restapi.armatubanda.dto.ProfileCreationDto;
import com.restapi.armatubanda.model.Instrument;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.model.MusicianContactInformation;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicianService {
    private final MusicianRepository musicianRepository;

    private final InstrumentService instrumentService;

    public Optional<Musician> getMusician(String username){
        return musicianRepository.findByEmail(username);
    }


    public ResponseEntity<ProfileCreationDto> createProfile(Musician musicianToSave, MusicianContactInformation contactInformation, List<Instrument> instruments){
        var musicianContactInformation = MusicianContactInformation.builder()
                .name(contactInformation.getName())
                .lastname(contactInformation.getLastname())
                .stageName(contactInformation.getStageName())
                .bio(contactInformation.getBio())
                .country(contactInformation.getCountry())
                .city(contactInformation.getCity())
                .phoneNumber(contactInformation.getPhoneNumber())
                .webSite(contactInformation.getWebSite())
                .socialMediaLink(contactInformation.getSocialMediaLink())
                .build();

        List<Instrument> instrumentsToSave = new ArrayList<>();

        for(Instrument instrument :instruments){
            instrumentsToSave.add(instrumentService.getInstrument(instrument.getName()).orElseThrow(()-> new UsernameNotFoundException("Instrument not found")));
        }
        musicianToSave.setMusicianContactInformation(musicianContactInformation);
        musicianToSave.setInstrument(instrumentsToSave);
        musicianToSave.setProfileSet(true);
        musicianRepository.save(musicianToSave);
        ProfileCreationDto fullProfile = new ProfileCreationDto();
        fullProfile.setMusicianContactInformation(musicianToSave.getMusicianContactInformation());
        fullProfile.setInstruments(musicianToSave.getInstrument());
        return ResponseEntity.ok(fullProfile);
    }



}
