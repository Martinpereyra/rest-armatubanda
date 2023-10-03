package com.restapi.armatubanda.controller;


import com.restapi.armatubanda.dto.BandCreationDto;
import com.restapi.armatubanda.dto.InvitationRequestDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.Invitation;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.InvitationRepository;
import com.restapi.armatubanda.services.BandService;
import com.restapi.armatubanda.services.InvitationService;
import com.restapi.armatubanda.services.MusicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bands")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class BandController {

    private final MusicianService musicianService;
    private final BandService bandService;
    private final InvitationService invitationService;

    @PostMapping(value = "/create-band", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BandCreationDto> createBand(@RequestPart("band") BandCreationDto band,
                                                      @RequestPart(value = "bandImageFile", required = false) MultipartFile file)throws Exception{
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician bandLeader = musicianService.getMusician(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return bandService.createBand(band,bandLeader,file);

    }

    @PostMapping(value="/invite")
    public Invitation inviteMusician(@RequestBody InvitationRequestDto invitationRequestDto){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musicianLeader = musicianService.getMusician(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(invitationRequestDto.getMusicianId() != musicianLeader.getId()){
            Band band = bandService.getBandById(invitationRequestDto.getBandId());
            if (musicianLeader.getId() == band.getMusicianLeader().getId()){
                Musician musicianInvited = this.musicianService.getMusicianById(invitationRequestDto.getMusicianId()).orElseThrow(()->new UsernameNotFoundException("Musician not found"));
                var newInvitation = Invitation.builder()
                        .bandInvitation(band)
                        .musicianInvited(musicianInvited)
                        .build();
                return invitationService.createInvitation(newInvitation);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    @GetMapping(value = "/{bandId}")
    public BandCreationDto getBand(@PathVariable int bandId){
        Band band = this.bandService.getBandById(bandId);

        return BandCreationDto.builder()
                .bandContactInfo(band.getBandContactInfo())
                .bandGenres(band.getGenres())
                .bandInfo(band.getBandInfo())
                .bandProfileImage(band.getImage())
                .leaderName(band.getMusicianLeader().getPersonalInformation().getName())
                .build();
    }

}
