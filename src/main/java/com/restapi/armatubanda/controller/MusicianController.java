package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.ProfileCreationDto;
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

@RestController
@RequestMapping("/api/musician")
@RequiredArgsConstructor
public class MusicianController {

    private final MusicianService musicianService;

    @PutMapping("/addinfo")
    public ResponseEntity<Musician> setBasicInformation(@RequestBody ProfileCreationDto profileInfoDto){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musicianToSave = musicianService.getMusician(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        var musicianContactInformation = MusicianContactInformation.builder()
                .name(profileInfoDto.getName())
                .lastname(profileInfoDto.getLastname())
                .country(profileInfoDto.getCountry())
                .city(profileInfoDto.getCity())
                .phoneNumber(profileInfoDto.getPhoneNumber())
                .webSite(profileInfoDto.getWebSite())
                .socialMediaLink(profileInfoDto.getSocialMediaLink())
                .build();
        musicianToSave.setMusicianContactInformation(musicianContactInformation);
        musicianService.save(musicianToSave);
        return ResponseEntity.ok(musicianToSave);
    }

    @GetMapping("/basicinfo")
    public MusicianContactInformation getContactInformation(){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musician = musicianService.getMusician(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return musician.getMusicianContactInformation();
    }




}
