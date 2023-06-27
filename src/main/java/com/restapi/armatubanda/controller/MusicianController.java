package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.ProfileCreationDto;
import com.restapi.armatubanda.model.Instrument;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.model.MusicianContactInformation;
import com.restapi.armatubanda.services.MusicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/musician")
@RequiredArgsConstructor
public class MusicianController {

    private final MusicianService musicianService;

    @PutMapping("/create-profile")
    public ResponseEntity<ProfileCreationDto> createProfile(@RequestBody ProfileCreationDto profileInfoDto) throws Exception {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musicianToSave = musicianService.getMusician(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(!musicianToSave.isProfileSet()){
        MusicianContactInformation contactInformation = profileInfoDto.getMusicianContactInformation();
        List<Instrument> musicianInstrument = profileInfoDto.getInstruments();
        return musicianService.createProfile(musicianToSave,contactInformation,musicianInstrument);
        }else{
            throw new Exception("No se puede registrar");
        }
    }


    @GetMapping("/basicinfo")
    public MusicianContactInformation getContactInformation(){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musician = musicianService.getMusician(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return musician.getMusicianContactInformation();
    }








}
