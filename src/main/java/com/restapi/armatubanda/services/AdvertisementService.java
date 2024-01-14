package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.AdvertisementRequestDto;
import com.restapi.armatubanda.dto.AdvertisementResponseDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.BandAdvertisement;
import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    private final GenreService genreService;

    public ResponseEntity<BandAdvertisement> createAd(Musician bandLeader, Band band, AdvertisementRequestDto advertisementRequestDto) throws Exception {

        try{
            if (band.getMusicianLeader() != bandLeader){
                throw new Exception();
            }

            List<Genre> genreList = genreService.getGenreList(advertisementRequestDto.getGenres());

            BandAdvertisement ad = BandAdvertisement.builder()
                    .band(band)
                    .description(advertisementRequestDto.getDescription())
                    .genres(genreList)
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

    public List<AdvertisementResponseDto> getAllAds() {
        return convertAds(advertisementRepository.findAll());
    }

    public List<AdvertisementResponseDto> convertAds(List<BandAdvertisement> adList){
        List<AdvertisementResponseDto> convertedList = new ArrayList<>();
        for(BandAdvertisement ad : adList){
            AdvertisementResponseDto convertedAd = AdvertisementResponseDto.builder()
                    .bandId(ad.getBand().getId())
                    .bandImage(ad.getBand().getImage())
                    .description(ad.getDescription())
                    .genres(ad.getGenres())
                    .build();

            convertedList.add(convertedAd);
        }
        return convertedList;
    }

}
