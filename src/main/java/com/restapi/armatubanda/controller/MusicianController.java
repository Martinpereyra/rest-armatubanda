package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.*;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.services.AuthenticationService;
import com.restapi.armatubanda.services.MusicianService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/musician")
@RequiredArgsConstructor
public class MusicianController {

    private final MusicianService musicianService;

    private final AuthenticationController authenticationController;

    private final AuthenticationService authenticationService;

    @GetMapping(value = "/find")
    public ResponseEntity<List<MusicianResponseDto>> getMusiciansList(@RequestBody MusicianRequestDto request) {
        return musicianService.getMusiciansList(request);
    }

    @PutMapping(value = "/create-profile",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MusicianResponseDto> createProfileAlt(@RequestPart(value = "profileInfoDto") ProfileCreationDto profileInfoDto,
                                                                @RequestPart(value = "profileImage", required = false)MultipartFile file) throws Exception{
        Musician musician = this.authenticationService.getMusicianLogged();
        MusicianResponseDto musicianResponseDto = musicianService.createProfileAlt(musician,profileInfoDto,file);
        return ResponseEntity.ok(musicianResponseDto);
    }

    @GetMapping("/basicinfo")
    public PersonalInformation getPersonalInformation(){
        Musician musician = this.authenticationService.getMusicianLogged();
        return musician.getPersonalInformation();
    }

    @PatchMapping(value = "/update-profile/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Musician> updateProfileImage (@RequestBody MultipartFile file) throws IOException {
        return musicianService.updateProfileImage(file);
    }


    @PutMapping(value = "/upload-review")
    public ResponseEntity<List<Review>> uploadMusicianReview (@RequestBody Review review) throws Exception {
        return musicianService.uploadMusicianReview(review);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicianResponseDto> getMusician(@PathVariable int id) {
        try {
            MusicianResponseDto musician = musicianService.getById(id);
            return ResponseEntity.ok(musician);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-profile/{id}")
    public ResponseEntity<MusicianProfileResponseDto> getMusicianProfile(@PathVariable int id){
        return musicianService.getMusicianProfile(id);
    }

    @GetMapping("/get-profile/information/{id}")
    public ResponseEntity<MusicianInformationResponseDto> getMusicianInformation(@PathVariable int id){
        return musicianService.getMusicianInformation(id);}


    @PostMapping(value = "/create-post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Post> createPost(@RequestPart(value= "videoUrl", required = false) String videoUrl,
                                           @RequestPart(value = "image", required = false) MultipartFile file) throws Exception{
        UserInfoDto user = authenticationController.getUserLogged().getBody();
        assert user != null;
        return musicianService.createPost(videoUrl, file, user.getId());
    }

    @GetMapping(value = "/get-post/{id}")
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable int id){
        return musicianService.getPosts(id);
    }



}
