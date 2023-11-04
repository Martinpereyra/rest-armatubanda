package com.restapi.armatubanda.controller;



import com.restapi.armatubanda.auth.AuthenticationRequest;
import com.restapi.armatubanda.auth.AuthenticationResponse;
import com.restapi.armatubanda.auth.RegisterRequest;
import com.restapi.armatubanda.dto.UserInfoDto;
import com.restapi.armatubanda.services.AuthenticationService;
import com.restapi.armatubanda.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getUserLogged(){
        return ResponseEntity.ok(service.getUserLogged());
    }

}
