package com.restapi.armatubanda.dto;

import com.restapi.armatubanda.model.BandInfo;
import com.restapi.armatubanda.model.ContactInformation;
import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandCreationDto {

    private BandInfo bandInfo;

    private ContactInformation bandContactInfo;

    private String leaderName;

    private Image bandProfileImage;

    private List<String> bandGenres;

}
