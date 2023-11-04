package com.restapi.armatubanda.services;

import com.restapi.armatubanda.auth.AuthenticationRequest;
import com.restapi.armatubanda.auth.AuthenticationResponse;
import com.restapi.armatubanda.auth.RegisterRequest;
import com.restapi.armatubanda.dto.UserInfoDto;
import com.restapi.armatubanda.exception.GenericException;
import com.restapi.armatubanda.exception.InvalidCredentialsException;
import com.restapi.armatubanda.exception.ResourceNotFoundException;
import com.restapi.armatubanda.model.Role;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.restapi.armatubanda.model.Musician;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final MusicianRepository musicianRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var musician = Musician.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isProfileSet(false)
                .build();
        try {
            musicianRepository.save(musician);
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
        return AuthenticationResponse.builder()
                .id(musician.getId())
                .token(jwtToken)
                .email(musician.getEmail())
                .isProfileSet(musician.booleanToString())
                .firstName(musician.getPersonalInformation().getName())
                .lastName(musician.getPersonalInformation().getLastname())
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

                return UserInfoDto.builder()
                        .id(musician.getId())
                        .user(username)
                        .isProfileSet(musician.booleanToString())
                        .firstName(musician.getPersonalInformation().getName())
                        .lastName(musician.getPersonalInformation().getLastname())
                        .profileImage(musician.getImage())
                        .build();
            }
        } catch (Exception e) {
            throw new GenericException("Hubo un error. Por favor inténtalo de nuevo más tarde.");
        }
        throw new ResourceNotFoundException("Usuario no encontrado.");
    }

}
