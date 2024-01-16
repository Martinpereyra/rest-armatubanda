package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.AdvertisementFilterDto;
import com.restapi.armatubanda.dto.AdvertisementRequestDto;
import com.restapi.armatubanda.dto.AdvertisementResponseDto;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.AdvertisementRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    private final GenreService genreService;

    private final InstrumentService instrumentService;

    private final EntityManager entityManager;

    public ResponseEntity<BandAdvertisement> createAd(Musician bandLeader, Band band, AdvertisementRequestDto advertisementRequestDto) throws Exception {

        try{
            if (band.getMusicianLeader() != bandLeader){
                throw new Exception();
            }

            List<Genre> genreList = genreService.getGenreList(advertisementRequestDto.getGenres());
            List<Instrument> instrumentList = instrumentService.getInstrumentList(advertisementRequestDto.getInstruments());

            BandAdvertisement ad = BandAdvertisement.builder()
                    .band(band)
                    .description(advertisementRequestDto.getDescription())
                    .genres(genreList)
                    .instruments(instrumentList)
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

    public List<AdvertisementResponseDto> getAdList(List<String> instruments,List<String> genres) {



        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BandAdvertisement> cq = cb.createQuery(BandAdvertisement.class);
        Root<BandAdvertisement> advertisementRoot = cq.from(BandAdvertisement.class);

        List<Predicate> predicates = new ArrayList<>();

        if(genres != null && !genres.isEmpty()){
            //List<Genre> genreList = this.genreService.getGenreListString(genres);
            predicates.add(advertisementRoot.get("genres").get("name").in(genres));
        }

        if(instruments != null && !instruments.isEmpty()){
            //List<Instrument> instrumentList = this.instrumentService.getInstrumentListString(instruments);
            predicates.add(advertisementRoot.get("instruments").get("name").in(instruments));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<BandAdvertisement> query = entityManager.createQuery(cq);

        List<BandAdvertisement> adList = query.getResultList();

        return convertAds(adList);
    }

    public List<AdvertisementResponseDto> convertAds(List<BandAdvertisement> adList){
        List<AdvertisementResponseDto> convertedList = new ArrayList<>();
        for(BandAdvertisement ad : adList){
            AdvertisementResponseDto convertedAd = AdvertisementResponseDto.builder()
                    .bandId(ad.getBand().getId())
                    .bandImage(ad.getBand().getImage())
                    .description(ad.getDescription())
                    .genres(ad.getGenres())
                    .instruments(ad.getInstruments())
                    .build();

            convertedList.add(convertedAd);
        }
        return convertedList;
    }

}
