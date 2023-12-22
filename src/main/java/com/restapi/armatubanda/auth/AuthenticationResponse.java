package com.restapi.armatubanda.auth;


import com.restapi.armatubanda.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private int id;
    private String token;
    private String email;
    private boolean isProfileSet;
    private String firstName;
    private String lastName;
    private Image profileImage;
}
