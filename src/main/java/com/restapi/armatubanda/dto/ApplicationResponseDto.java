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
public class ApplicationResponseDto {

    public Image musicianImage;
    public String applicationMessage;
    public int musicianId;
    public String musicianName;
    public int applicationId;

}
