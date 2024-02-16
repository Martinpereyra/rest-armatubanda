package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.AdListResponseDto;
import com.restapi.armatubanda.dto.AdvertisementFilterDto;
import com.restapi.armatubanda.dto.AdvertisementRequestDto;
import com.restapi.armatubanda.dto.AdvertisementResponseDto;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.AdvertisementRepository;
import com.restapi.armatubanda.repository.ApplicationRepository;
import com.restapi.armatubanda.repository.BandRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    private final GenreService genreService;

    private final InstrumentService instrumentService;

    private final EntityManager entityManager;

    private final AuthenticationService authenticationService;

    private final InvitationService invitationService;

    private final ApplicationRepository applicationRepository;

    private final BandRepository bandRepository;

    public ResponseEntity<BandAdvertisement> createAd(Musician bandLeader, Band band, AdvertisementRequestDto advertisementRequestDto) throws Exception {

        try{
            if (band.getMusicianLeader() != bandLeader){
                throw new Exception();
            }

            List<Genre> genreList = genreService.getGenreList(advertisementRequestDto.getGenres());
            List<Instrument> instrumentList = instrumentService.getInstrumentList(advertisementRequestDto.getInstruments());

            BandAdvertisement ad = BandAdvertisement.builder()
                    .band(band)
                    .name(advertisementRequestDto.getName())
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

    public List<AdvertisementResponseDto> getAdList(List<String> instruments,List<String> genres, String name) {

        Musician musicianLogged = this.authenticationService.getMusicianLogged();
        int musicianLoggedId = musicianLogged.getId();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BandAdvertisement> cq = cb.createQuery(BandAdvertisement.class);
        Root<BandAdvertisement> advertisementRoot = cq.from(BandAdvertisement.class);

        List<Predicate> predicates = new ArrayList<>();

        if(genres != null && !genres.isEmpty()){
            predicates.add(advertisementRoot.get("genres").get("name").in(genres));
        }

        if(instruments != null && !instruments.isEmpty()){
            predicates.add(advertisementRoot.get("instruments").get("name").in(instruments));
        }

        if (name != null && !name.isBlank()) {
            predicates.add((cb.like(advertisementRoot.get("band").get("bandInfo").get("name"), "%" + name + "%")));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<BandAdvertisement> query = entityManager.createQuery(cq);

        List<BandAdvertisement> adList = query.getResultList();

        List<AdvertisementResponseDto> adListDto = convertAds(adList);

        List<AdvertisementResponseDto> adListResponseDto = new ArrayList<>();

        for(AdvertisementResponseDto ad : adListDto){
            int bandId = ad.getBandId();
            int adId = ad.getAdId();

            String invitationStatus = this.invitationService.getInvitationStatus(bandId,musicianLoggedId);
            Band band = this.bandRepository.findById(bandId).orElseThrow(()-> new UsernameNotFoundException("Band not found with id: "+bandId));


            if(Objects.equals(invitationStatus, "MEMBER") || band.getMusicianLeader().getId() == musicianLoggedId){
                ad.setStatus("MEMBER");
                adListResponseDto.add(ad);
            }
            else{
                if(Objects.equals(invitationStatus,"PENDING")){
                    ad.setStatus("INVITED");
                    adListResponseDto.add(ad);
                }else{
                    if(this.applicationRepository.existsByMusicianIdAndAdvertisementId(musicianLoggedId,adId)){
                        ad.setStatus("PENDING");
                        adListResponseDto.add(ad);
                    }else {
                        ad.setStatus("ELIGIBLE");
                        adListResponseDto.add(ad);
                    }
                }
            }
        }

        return adListResponseDto;
    }

    public List<AdvertisementResponseDto> convertAds(List<BandAdvertisement> adList){
        List<AdvertisementResponseDto> convertedList = new ArrayList<>();
        for(BandAdvertisement ad : adList){
            AdvertisementResponseDto convertedAd = AdvertisementResponseDto.builder()
                    .adId(ad.getId())
                    .adName(ad.getName())
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

    public BandAdvertisement getAdvertisement(int idAd){
        return this.advertisementRepository.findById(idAd).orElseThrow(()-> new UsernameNotFoundException("Advertisement didnt found"));
    }

    public List<AdvertisementResponseDto> getAdBandList(int bandId) {
        List<BandAdvertisement> bandAdvertisementList = this.advertisementRepository.findAllByBandId(bandId);
        return convertAds(bandAdvertisementList);
    }
}
