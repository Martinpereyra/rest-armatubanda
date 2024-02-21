package com.restapi.armatubanda.dto;


import com.restapi.armatubanda.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private int id;
    private String user;
    private boolean isProfileSet;
    private String firstName;
    private String lastName;
    private Image profileImage;
    private boolean emailVerified;
    private String role;
}
