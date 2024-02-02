package com.restapi.armatubanda.dto;


import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.model.Instrument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementRequestDto {

    public String description;

    public String name;

    public int bandId;

    public List<Genre> genres;

    public List<Instrument> instruments;

}
