package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.*;
import com.restapi.armatubanda.model.*;
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

    @GetMapping()
    public ResponseEntity<List<MusicianResponseDto>> getMusiciansList(@ModelAttribute MusicianRequestDto request) {
        return musicianService.getMusiciansList(request);
    }

    // TODO: Implement try-catch
    @PutMapping(value = "/create-profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Musician> createProfile(@RequestPart(value = "profileInfoDto") ProfileCreationDto profileInfoDto,
                                                  @RequestPart(value = "profileImage", required = false)MultipartFile file) throws Exception {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musicianToSave = musicianService.getMusician(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(!musicianToSave.isProfileSet()){
        PersonalInformation personalInformation = profileInfoDto.getPersonalInformation();
        ContactInformation contactInformation = profileInfoDto.getContactInformation();
        SkillsInformation skillsInformation = profileInfoDto.getSkillsInformation();
        EducationInformation educationInformation = profileInfoDto.getEducationInformation();
        CareerInformation careerInformation = profileInfoDto.getCareerInformation();
        BiographyInformation biographyInformation = profileInfoDto.getBiographyInformation();
        PreferenceInformation preferenceInformation = profileInfoDto.getPreferenceInformation();
        Image image = null;
        if(file != null) {
            image = musicianService.uploadProfileImage(file);
        }
        return musicianService.createProfile(musicianToSave,personalInformation,contactInformation,skillsInformation,educationInformation,careerInformation,biographyInformation,preferenceInformation, image);
        }
        else{
            throw new Exception("No se puede registrar");
        }
    }

    @GetMapping("/basicinfo")
    public PersonalInformation getPersonalInformation(){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musician = musicianService.getMusician(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
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
            // TODO: Return error message (Implement exceptions handling)
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


    @PostMapping(value = "/create-post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Post> createPost(@RequestPart("post") PostDto postDto,
                                           @RequestPart(value = "postImage", required = false)MultipartFile file) throws Exception{
        Image image = null;
        if(file != null) {
            image = musicianService.uploadProfileImage(file);
        }
        UserInfoDto user = authenticationController.getUserLogged().getBody();
        assert user != null;
        return musicianService.createPost(postDto,image,user.getId());
    }

    @GetMapping(value = "/get-post/{id}")
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable int id){
        return musicianService.getPosts(id);
    }



}
