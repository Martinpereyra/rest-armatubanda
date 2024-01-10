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
public class MusicianBandsDto {

    public int bandId;
    public String bandName;
    public Image bandImage;

}

