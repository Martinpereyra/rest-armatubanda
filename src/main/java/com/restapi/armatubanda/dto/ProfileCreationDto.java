package com.restapi.armatubanda.dto;


import com.restapi.armatubanda.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCreationDto {

    private PersonalInformation personalInformation;

    private ContactInformation contactInformation;

    private SkillsInformation skillsInformation;

    private EducationInformation educationInformation;

    private CareerInformation careerInformation;

    private BiographyInformation biographyInformation;

    private PreferenceInformation preferenceInformation;

    private List<Instrument> instruments;

    private Image profileImage;

}
