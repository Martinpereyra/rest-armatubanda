package com.restapi.armatubanda.dto;

import com.restapi.armatubanda.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private int Id;
    private String videoUrl;
    private Image image;
    private Instant createdOn;
}
