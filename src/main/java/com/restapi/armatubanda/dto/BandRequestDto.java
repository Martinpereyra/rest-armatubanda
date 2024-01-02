package com.restapi.armatubanda.dto;


import com.restapi.armatubanda.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandRequestDto {

    public String bandName;

    public List<String> bandGenres;

    public String bandCountry;

    public String bandCity;
}
