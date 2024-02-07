package com.restapi.armatubanda.services;

import com.restapi.armatubanda.auth.AuthenticationRequest;
import com.restapi.armatubanda.auth.AuthenticationResponse;
import com.restapi.armatubanda.auth.RegisterRequest;
import com.restapi.armatubanda.dto.UserInfoDto;
import com.restapi.armatubanda.exception.GenericException;
import com.restapi.armatubanda.exception.InvalidCredentialsException;
import com.restapi.armatubanda.exception.ResourceNotFoundException;
import com.restapi.armatubanda.model.ConfirmationToken;
import com.restapi.armatubanda.model.Role;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.restapi.armatubanda.model.Musician;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final MusicianRepository musicianRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) {

        var musician = Musician.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isProfileSet(false)
                .isEmailConfirmed(false)
                .build();
        try {
            Musician musicianSaved = musicianRepository.save(musician);
            String token = this.confirmationTokenService.createConfirmationToken(musicianSaved);

            // TODO: SEND EMAIL
            String link = "http://localhost:8080/api/auth/confirm?token="+token;
            emailService.sendEmail(musician.getEmail(),buildEmail(musician.getEmail(),link));


        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("El email " + request.getEmail() + " ya fue usado.");
        }
        var jwtToken = jwtService.generateToken(musician);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(musician.getEmail())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Usuario y/o contraseña incorrectos.");
        } catch (Exception e) {
            throw new GenericException("Hubo un error. Por favor inténtalo de nuevo más tarde.");
        }

        // Si llego hasta aca el usuario es correcto, caso contrario el metodo authenticate del manager tira una exception
        var musician = musicianRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(musician);
        var firstName = "";
        var lastName = "";
        if (musician.getPersonalInformation() != null) {
            firstName = musician.getPersonalInformation().getName();
            lastName = musician.getPersonalInformation().getLastname();
        }
        return AuthenticationResponse.builder()
                .id(musician.getId())
                .token(jwtToken)
                .email(musician.getEmail())
                .isProfileSet(musician.isProfileSet())
                .firstName(firstName)
                .lastName(lastName)
                .profileImage(musician.getImage())
                .build();
    }

    public UserInfoDto getUserLogged() {
        try {
            var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                var musician = musicianRepository.findByEmail(username)
                        .orElseThrow(null);

                var firstName = "";
                var lastName = "";
                if (musician.getPersonalInformation() != null) {
                    firstName = musician.getPersonalInformation().getName();
                    lastName = musician.getPersonalInformation().getLastname();
                }
                return UserInfoDto.builder()
                        .id(musician.getId())
                        .user(username)
                        .isProfileSet(musician.isProfileSet())
                        .firstName(firstName)
                        .lastName(lastName)
                        .profileImage(musician.getImage())
                        .build();
            }
        } catch (Exception e) {
            throw new GenericException("Hubo un error. Por favor inténtalo de nuevo más tarde.");
        }
        throw new ResourceNotFoundException("Usuario no encontrado.");
    }

    public Musician getMusicianLogged(){
        try {
            var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                return musicianRepository.findByEmail(username)
                        .orElseThrow(null);
            }
        } catch (Exception e) {
            throw new GenericException("Hubo un error. Por favor inténtalo de nuevo más tarde.");
        }
        throw new ResourceNotFoundException("Usuario no encontrado.");
    }

    public ResponseEntity<UserInfoDto> confirmEmail(String token) {
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenService.getToken(token);

        if(confirmationTokenOptional.isEmpty()){
            throw new RuntimeException();
        }
        ConfirmationToken confirmationToken = confirmationTokenOptional.get();
        if(confirmationTokenService.isTokenValid(confirmationToken.getConfirmationToken())){
            confirmationTokenService.setConfirmedAt(confirmationToken.getConfirmationToken());
            return ResponseEntity.ok(UserInfoDto.builder()
                    .id(confirmationToken.getUser().getId())
                    .firstName(confirmationToken.getUser().getPersonalInformation().getName())
                    .lastName(confirmationToken.getUser().getPersonalInformation().getLastname())
                    .user(confirmationToken.getUser().getEmail())
                    .build());
        }else{
            throw new RuntimeException();
        }
    }

    private String buildEmail(String name, String link) {
        return "<div>Thank you for registering, " + name + "! Please click on the below link to activate your account: <a href=\"" + link + "\">Activate Now</a></div>";
    }

}
