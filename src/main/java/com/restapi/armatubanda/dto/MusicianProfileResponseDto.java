package com.restapi.armatubanda.dto;

import com.restapi.armatubanda.model.ContactInformation;
import com.restapi.armatubanda.model.Image;
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
public class MusicianProfileResponseDto {

    public String firstName;
    public String lastName;
    public String stageName;
    public String biography;
    public ContactInformation contactInformation;
    public List<Review> reviews;
    public Image image;
}
