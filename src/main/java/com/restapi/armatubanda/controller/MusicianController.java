package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.MusicianRequestDto;
import com.restapi.armatubanda.dto.MusicianResponseDto;
import com.restapi.armatubanda.dto.ProfileCreationDto;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.services.MusicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/musician")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MusicianController {

    private final MusicianService musicianService;

    @GetMapping()
    public ResponseEntity<List<MusicianResponseDto>> getMusiciansList(@ModelAttribute MusicianRequestDto request) {
        return musicianService.getMusiciansList(request);
    }

    // TODO: Implement try-catch
    @PutMapping(value = "/create-profile", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProfileCreationDto> createProfile(@RequestPart("musician") ProfileCreationDto profileInfoDto,
                                                            @RequestPart(value = "profileImageFile", required = false)MultipartFile file) throws Exception {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musicianToSave = musicianService.getMusician(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(!musicianToSave.isProfileSet()){
        MusicianContactInformation contactInformation = profileInfoDto.getMusicianContactInformation();
        List<Instrument> musicianInstrument = profileInfoDto.getInstruments();
        Image image = null;
        if(file != null) {
            image = musicianService.uploadProfileImage(file);
        }
            return musicianService.createProfile(musicianToSave,contactInformation,musicianInstrument, image);
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

    @PatchMapping(value = "/update-profile/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Musician> updateProfileImage (@RequestBody MultipartFile file) throws IOException {
        return musicianService.updateProfileImage(file);
    }


    @PutMapping(value = "/upload-review")
    public ResponseEntity<List<Review>> uploadMusicianReview (@RequestBody Review review) throws Exception {
        return musicianService.uploadMusicianReview(review);
    }





}
