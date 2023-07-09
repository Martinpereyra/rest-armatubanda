package com.restapi.armatubanda.services;

import com.restapi.armatubanda.auth.AuthenticationRequest;
import com.restapi.armatubanda.auth.AuthenticationResponse;
import com.restapi.armatubanda.auth.RegisterRequest;
import com.restapi.armatubanda.dto.UserInfoDto;
import com.restapi.armatubanda.model.Role;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.restapi.armatubanda.model.Musician;
import org.springframework.web.ErrorResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final MusicianRepository musicianRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    public AuthenticationResponse register(RegisterRequest request) {
        var musician = Musician.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isProfileSet(false)
                .build();
        musicianRepository.save(musician);
        var jwtToken = jwtService.generateToken(musician);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(musician.getEmail())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // Si llego hasta aca el usuario es correcto, caso contrario el metodo authenticate del manager tira una exception
        var musician = musicianRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(musician);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(musician.getEmail())
                .build();
    }

    public ResponseEntity<UserInfoDto> getUserLogged() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null){
            return null;
        }
        String username = ((UserDetails) principal).getUsername();
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .user(username)
                .build();

        return ResponseEntity.ok(userInfoDto);

    }
}
