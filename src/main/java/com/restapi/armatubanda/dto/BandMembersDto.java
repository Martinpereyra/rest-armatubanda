package com.restapi.armatubanda.dto;

import com.restapi.armatubanda.model.Image;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BandMembersDto {

    public int musicianId;
    public String musicianName;
    public String musicianLastName;
    public Image musicianProfileImage;



}
