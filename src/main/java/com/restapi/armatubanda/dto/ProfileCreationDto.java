package com.restapi.armatubanda.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCreationDto {

    private String name;
    private String lastname;
    private String country;
    private String city;
    private String phoneNumber;
    private String webSite;
    private String socialMediaLink;

}
