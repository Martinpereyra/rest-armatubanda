package com.restapi.armatubanda.dto;

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
    private String name;
    private String city;
    private List<String> instruments;
}
