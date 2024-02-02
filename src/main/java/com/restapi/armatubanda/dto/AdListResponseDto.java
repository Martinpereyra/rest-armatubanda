package com.restapi.armatubanda.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdListResponseDto {
    public AdvertisementResponseDto adResponse;
    public String status;
}
