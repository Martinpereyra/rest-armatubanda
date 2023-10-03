package com.restapi.armatubanda.services;

import com.restapi.armatubanda.model.Invitation;
import com.restapi.armatubanda.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;


    public Invitation createInvitation(Invitation invitation){
        return this.invitationRepository.save(invitation);
    }

}
