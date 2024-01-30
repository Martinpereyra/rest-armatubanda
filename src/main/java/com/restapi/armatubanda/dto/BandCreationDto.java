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


    public BandInfo bandInfo;

    public ContactInformation bandContactInfo;

    public int leaderId;

    public String leaderName;

    public String leaderLastname;

    public Image leaderProfileImage;

    public Image bandProfileImage;

    public List<String> bandGenres;

    // TODO: Agregar lista de posts y reviews, bandId, lista de miembros con su id,foto

}
