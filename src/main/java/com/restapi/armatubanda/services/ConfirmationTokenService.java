package com.restapi.armatubanda.services;

import com.restapi.armatubanda.model.ConfirmationToken;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.ConfirmationTokenRepository;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final MusicianRepository musicianRepository;

    public String createConfirmationToken(Musician musician) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60),
                musician
        );
        confirmationTokenRepository.save(confirmationToken);
        return token;
    }

    public Optional<ConfirmationToken> getToken(String token){
        return confirmationTokenRepository.findByConfirmationToken(token);
    }

    @Transactional
    public void setConfirmedAt(String token){
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Token not found"));

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
        Musician musicianToSave = confirmationToken.getUser();
        musicianToSave.setEmailConfirmed(true);
        musicianRepository.save(musicianToSave);
    }

    public boolean isTokenValid(String token){
        Optional<ConfirmationToken> confirmationTokenOptional = getToken(token);
        if(confirmationTokenOptional.isPresent()){
            ConfirmationToken confirmationToken = confirmationTokenOptional.get();
            return confirmationToken.getExpiresAt().isAfter(LocalDateTime.now()) && confirmationToken.getConfirmedAt() == null;
        }
        return false;
    }



}
