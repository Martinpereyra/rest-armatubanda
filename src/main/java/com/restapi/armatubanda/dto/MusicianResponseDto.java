package com.restapi.armatubanda.dto;

import com.restapi.armatubanda.model.Image;
import com.restapi.armatubanda.model.Instrument;
import com.restapi.armatubanda.model.PersonalInformation;
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
    private PersonalInformation personalInformation;
    private Image profileImage;
    private List<Review> reviews;
}
