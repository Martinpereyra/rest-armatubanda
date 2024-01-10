package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.InvitationRequestDto;
import com.restapi.armatubanda.dto.InvitationStatusDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.Invitation;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.services.AuthenticationService;
import com.restapi.armatubanda.services.BandService;
import com.restapi.armatubanda.services.InvitationService;
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
@RequestMapping("/api/invitation")
@RequiredArgsConstructor
public class InvitationController {

    private final MusicianService musicianService;
    private final BandService bandService;
    private final InvitationService invitationService;
    private final AuthenticationService authenticationService;

    @PostMapping(value="/invite")
    public ResponseEntity<Invitation> inviteMusician(@RequestBody InvitationRequestDto invitationRequestDto) throws Exception {
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
                return ResponseEntity.ok(invitationService.createInvitation(newInvitation));
            }else{
                throw new Exception();
            }
        }else{
            throw new Exception();
        }
    }

    @PutMapping(value = "/change")
    public ResponseEntity<InvitationStatusDto> changeInvitationStatus(@RequestBody InvitationStatusDto invitationStatusDto) throws Exception {
        Musician musicianInvited = authenticationService.getMusicianLogged();
        if (invitationStatusDto.getMusicianId() == musicianInvited.getId()){
            return this.invitationService.changeInvitationStatus(invitationStatusDto);
        }
        else {
            throw new Exception();
        }
    }

    @GetMapping(value="/musician/pending")
    public List<Invitation> getMusicianPendingInvitations(){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musician = musicianService.getMusician(username).orElseThrow(()->new UsernameNotFoundException("User not found"));

        return this.invitationService.getMusicianPendingInvitations(musician.getId());
    }

    @GetMapping(value="/musician/accepted")
    public List<Invitation> getMusicianAcceptedInvitations(){
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musician = musicianService.getMusician(username).orElseThrow(()->new UsernameNotFoundException("User not found"));

        return this.invitationService.getMusicianAcceptedInvitations(musician.getId());
    }

}
