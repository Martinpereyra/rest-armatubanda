package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.AdvertisementRequestDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.BandAdvertisement;
import com.restapi.armatubanda.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    public ResponseEntity<BandAdvertisement> createAd(Band band, AdvertisementRequestDto advertisementRequestDto) throws Exception {

        try{
            BandAdvertisement ad = BandAdvertisement.builder()
                    .band(band)
                    .description(advertisementRequestDto.getDescription())
                    .build();

            return ResponseEntity.ok(advertisementRepository.save(ad));
        }catch(Exception e){
            throw new Exception();
        }
    }
}
