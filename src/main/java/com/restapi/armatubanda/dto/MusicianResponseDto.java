package com.restapi.armatubanda.dto;

import com.restapi.armatubanda.model.Image;
import com.restapi.armatubanda.model.Instrument;
import com.restapi.armatubanda.model.MusicianContactInformation;
import com.restapi.armatubanda.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicianResponseDto {
    private int id;
    private MusicianContactInformation musicianContactInformation;

    private List<Instrument> instruments;
    private Image profileImage;
    private List<Review> reviews;
}