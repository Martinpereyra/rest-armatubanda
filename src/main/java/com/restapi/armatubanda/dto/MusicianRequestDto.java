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
public class MusicianRequestDto {
    public String name;
    public String city;
    public List<String> genres;
    public List<String> instruments;

}
