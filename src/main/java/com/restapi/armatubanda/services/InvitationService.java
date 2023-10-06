package com.restapi.armatubanda.services;

import com.restapi.armatubanda.dto.InvitationRequestDto;
import com.restapi.armatubanda.dto.InvitationStatusDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.Invitation;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.BandRepository;
import com.restapi.armatubanda.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;

    private final BandService bandService;

    private final MusicianService musicianService;


    public Invitation createInvitation(Invitation invitation){
        return this.invitationRepository.save(invitation);
    }

    public ResponseEntity<InvitationStatusDto> changeInvitationStatus(InvitationStatusDto invitationStatusDto) throws Exception {
        if(invitationStatusDto.isStatus()){
            Invitation invitationTosave = this.invitationRepository.findById(invitationStatusDto.getInvitationId()).orElseThrow(()-> new UsernameNotFoundException("Invitation not found"));
            if(!invitationTosave.isStatus()){
                invitationTosave.setStatus(true);
                Band bandToSave = this.bandService.getBandById(invitationTosave.getBandInvitation().getId());
                List<Musician> bandMembers = bandToSave.getMembers();
                Musician musicianToSave = this.musicianService.getMusicianById(invitationTosave.getMusicianInvited().getId()).orElseThrow(()-> new UsernameNotFoundException("Musician not found"));
                bandMembers.add(musicianToSave);
                bandToSave.setMembers(bandMembers);
                this.bandService.save(bandToSave);
            }else {
                throw new Exception();
            }
            Invitation invitationTemp = this.invitationRepository.save(invitationTosave);
            var invitationReturned = InvitationStatusDto.builder()
                    .invitationId(invitationTemp.getId())
                    .musicianId(invitationTemp.getMusicianInvited().getId())
                    .bandId(invitationTemp.getBandInvitation().getId())
                    .status(invitationStatusDto.isStatus())
                    .build();

            return ResponseEntity.ok(invitationReturned);
        }
        else{

            Invitation invitationToDelete = this.invitationRepository.findById(invitationStatusDto.getInvitationId()).orElseThrow(()-> new UsernameNotFoundException("Invitation not found"));
            Band bandToSave = this.bandService.getBandById(invitationToDelete.getBandInvitation().getId());
            List<Musician> bandMembers = bandToSave.getMembers();
            Musician musicianToDelete = this.musicianService.getMusicianById(invitationToDelete.getMusicianInvited().getId()).orElseThrow(()-> new UsernameNotFoundException("Musician not found"));
            bandMembers.remove(musicianToDelete);
            bandToSave.setMembers(bandMembers);
            this.bandService.save(bandToSave);
            this.invitationRepository.delete(invitationToDelete);
            return ResponseEntity.ok(null);
        }
    }

    public List<Invitation> getMusicianInvitations(int musicianId) {
        return this.invitationRepository.findAll()
                .stream()
                .filter(m->m.getMusicianInvited().getId() == musicianId)
                .collect(Collectors.toList());
    }
}
