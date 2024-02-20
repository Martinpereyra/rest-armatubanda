package com.restapi.armatubanda.controller;


import com.restapi.armatubanda.dto.*;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.model.Post;
import com.restapi.armatubanda.model.Review;
import com.restapi.armatubanda.services.AuthenticationService;
import com.restapi.armatubanda.services.BandService;
import com.restapi.armatubanda.services.InvitationService;
import com.restapi.armatubanda.services.MusicianService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
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
@RequestMapping("/api/bands")
@CrossOrigin(origins = "https://utn-armatubanda.netlify.app")
@RequiredArgsConstructor
public class BandController {

    private final MusicianService musicianService;
    private final BandService bandService;
    private final AuthenticationService authenticationService;

    private final InvitationService invitationService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BandCreationDto> createBand(@RequestPart("band") BandCreationDto band,
                                                    @RequestPart(value = "bandImageFile", required = false) MultipartFile file) throws Exception {
        try {
            Musician bandLeader = authenticationService.getMusicianLogged();
            BandCreationDto createdBand = bandService.createBand(band, bandLeader, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBand);
        } catch (Exception e) {
            Exception errorResponse = new Exception(
                    e.getMessage()
            );
            throw new Exception(errorResponse);
        }
    }

    @GetMapping(value = "/{bandId}")
    public BandCreationDto getBand(@PathVariable int bandId) {
        return this.bandService.getBand(bandId);
    }

    @DeleteMapping(value = "/delete/{bandId}")
    public HttpStatus deleteBand(@PathVariable int bandId) throws Exception {

        Musician bandLeader = authenticationService.getMusicianLogged();
        Band bandToDelete = this.bandService.getBandById(bandId);

        if (bandToDelete.getMusicianLeader().getId() == bandLeader.getId()) {
            try {
                this.invitationService.deleteAllBandInvitations(bandToDelete.getId());
                this.bandService.deleteBand(bandToDelete);
                return HttpStatus.OK;
            }catch (Exception e){
                Exception errorResponse = new Exception(
                        e.getMessage()
                );
                throw new Exception(errorResponse);
            }
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @PutMapping(value = "/delete/member")
    public HttpStatus deleteBandMember(@RequestBody BandMemberDto bandMemberDto) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician bandLeader = musicianService.getMusician(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Band band = this.bandService.getBandById(bandMemberDto.getBandId());

        if (band.getMusicianLeader().getId() == bandLeader.getId()) {
            List<Musician> bandMembers = band.getMembers();
            Musician memberToDelete = this.musicianService.getMusicianById(bandMemberDto.getMemberId()).orElseThrow(() -> new UsernameNotFoundException("Musician not found"));
            if (bandMembers.remove(memberToDelete)) {
                band.setMembers(bandMembers);
                this.bandService.updateBand(band);
                this.invitationService.deleteInvitation(band.getId(),memberToDelete.getId());
                return HttpStatus.OK;
            }
        }
        return HttpStatus.BAD_REQUEST;
    }

    @GetMapping()
    public ResponseEntity<List<BandCreationDto>> getBandList(
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "country",required = false) String country,
            @RequestParam(value = "city",required = false) String city,
            @RequestParam(value = "genres",required = false) List<String> genres
    ){
        return bandService.getBandList(name,country,city,genres);
    }

    @PostMapping(value = "/create-post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Post> createPost(
            @RequestParam(value = "bandId", required = true) int bandId,
            @RequestPart(value= "videoUrl", required = false) String videoUrl,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws Exception{
        return bandService.createPost(videoUrl, file,bandId);
    }

    @GetMapping(value = "/get-post/{id}")
    public ResponseEntity<List<Post>> getPosts(@PathVariable int id){
        return bandService.getPosts(id);
    }

    @PutMapping(value = "/upload-review")
    public ResponseEntity<List<Review>> uploadMusicianReview (@RequestBody Review review) throws Exception {
        return bandService.uploadMusicianReview(review);
    }

    @GetMapping(value = "/members/{bandId}")
    public ResponseEntity<List<BandMembersDto>> getBandMembers(@PathVariable("bandId") int bandId){
        return bandService.getBandMembers(bandId);
    }

    @PutMapping(value="/edit", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BandCreationDto> editProfile(@RequestPart("band") BandCreationDto bandDto,
                                                       @RequestPart(value = "bandImageFile", required = false) MultipartFile file) throws IOException {

        return this.bandService.editProfile(bandDto,file);
    }
}
