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
public class BandCreationDto {

    public int bandId;

    public BandInfo bandInfo;

    public ContactInformation bandContactInfo;

    public BandMembersDto leader;

    public Image bandProfileImage;

    public List<String> bandGenres;

    public List<Post> postList;

    public List<Review> reviewsList;

    public List<BandMembersDto> bandMembersList;

    // TODO: Agregar lista de posts y reviews, bandId, lista de miembros con su id,foto

}
