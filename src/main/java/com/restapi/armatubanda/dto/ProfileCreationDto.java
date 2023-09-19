package com.restapi.armatubanda.dto;


import com.restapi.armatubanda.model.ContactInformation;
import com.restapi.armatubanda.model.Image;
import com.restapi.armatubanda.model.Instrument;
import com.restapi.armatubanda.model.PersonalInformation;
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

    private List<Instrument> instruments;

    private Image profileImage;



}
