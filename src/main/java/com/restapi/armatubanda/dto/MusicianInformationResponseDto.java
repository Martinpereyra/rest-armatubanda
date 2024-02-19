package com.restapi.armatubanda.dto;


import com.restapi.armatubanda.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicianInformationResponseDto {

    public BiographyInformation biographyInformation;
    public CareerInformation careerInformation;
    public EducationInformation educationInformation;
    public SkillsInformation skillsInformation;
    public PreferenceInformation preferenceInformation;

}
