package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.AdvertisementRequestDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.BandAdvertisement;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    public ResponseEntity<BandAdvertisement> createAd(Musician bandLeader, Band band, AdvertisementRequestDto advertisementRequestDto) throws Exception {

        try{
            if (band.getMusicianLeader() != bandLeader){
                throw new Exception();
            }
            BandAdvertisement ad = BandAdvertisement.builder()
                    .band(band)
                    .description(advertisementRequestDto.getDescription())
                    .build();
            return ResponseEntity.ok(advertisementRepository.save(ad));
        }catch(Exception e){
            throw new Exception();
        }
    }

    public HttpStatus deleteAd(Musician bandLeader, int adId) throws Exception {
        BandAdvertisement bandAdvertisement = advertisementRepository.findById(adId).orElseThrow(()-> new Exception());

        if (bandLeader.getId() != bandAdvertisement.getBand().getMusicianLeader().getId()){
            throw new Exception();
        }
        advertisementRepository.deleteById(adId);
        return HttpStatus.OK;
    }
}
