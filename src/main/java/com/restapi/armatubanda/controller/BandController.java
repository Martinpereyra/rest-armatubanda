package com.restapi.armatubanda.controller;


import com.restapi.armatubanda.dto.BandCreationDto;
import com.restapi.armatubanda.dto.BandMemberDto;
import com.restapi.armatubanda.dto.BandRequestDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.services.AuthenticationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/bands")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class BandController {

    private final MusicianService musicianService;
    private final BandService bandService;
    private final AuthenticationService authenticationService;
    private final InvitationService invitationService;

    @PostMapping(value = "/createBand", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
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
        Band band = this.bandService.getBandById(bandId);

        return BandCreationDto.builder()
                .bandContactInfo(band.getBandContactInfo())
                .bandGenres(band.getGenres())
                .bandInfo(band.getBandInfo())
                .bandProfileImage(band.getImage())
                .leaderName(band.getMusicianLeader().getPersonalInformation().getName())
                .build();
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


    @GetMapping(value = "/find")
    public ResponseEntity<List<BandCreationDto>> getBandList(@RequestBody BandRequestDto bandRequestDto){
        return bandService.getBandList(bandRequestDto);
    }
}
