package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.BandCreationDto;
import com.restapi.armatubanda.dto.BandRequestDto;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.BandRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepository;

    private final GenreService genreService;

    private final EntityManager entityManager;


    public BandCreationDto createBand(BandCreationDto bandCreationDto, Musician bandLeader, MultipartFile file) throws IOException {

        List<Genre> genreList = genreService.getGenreList(bandCreationDto.getBandGenres());
        
        var bandToSave = Band.builder()
                .bandInfo(bandCreationDto.getBandInfo())
                .bandContactInfo(bandCreationDto.getBandContactInfo())
                .musicianLeader(bandLeader)
                .genres(genreList)
                .build();

        if (file != null){
            Image image = this.uploadProfileImage(file);
            bandToSave.setImage(image);
        }


        Band savedBand = bandRepository.save(bandToSave);
        return convertToBandCreationDto(savedBand);

    }

    private BandCreationDto convertToBandCreationDto(Band band) {
        BandCreationDto bandCreationDto = new BandCreationDto();

        bandCreationDto.setBandInfo(band.getBandInfo());
        bandCreationDto.setBandContactInfo(band.getBandContactInfo());

        if (band.getMusicianLeader() != null && band.getMusicianLeader().getPersonalInformation() != null) {
            String leaderName = band.getMusicianLeader().getPersonalInformation().getName();
            bandCreationDto.setLeaderName(leaderName);
        }

        bandCreationDto.setBandProfileImage(band.getImage());
        bandCreationDto.setBandGenres(band.getGenres());

        return bandCreationDto;
    }

    public Image uploadProfileImage(MultipartFile file) throws IOException {
        return Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .picByte(file.getBytes())
                .build();
    }

    public Band getBandById(int bandId) {
        return this.bandRepository.findById(bandId).orElseThrow(() -> new UsernameNotFoundException("Band not found"));
    }

    public void save(Band band){
        this.bandRepository.save(band);
    }

    public void deleteBand(Band bandToDelete) {
        this.bandRepository.delete(bandToDelete);
    }

    public void updateBand(Band band) {
        this.bandRepository.save(band);
    }

    public ResponseEntity<List<BandCreationDto>> getBandList(BandRequestDto bandRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Band> cq = cb.createQuery(Band.class);
        Root<Band> band = cq.from(Band.class);

        List<Predicate> predicates = new ArrayList<>();

        if(bandRequest.getBandName() != null && !bandRequest.getBandName().isEmpty()){
            predicates.add(cb.like(band.get("bandInfo").get("name"),"%" + bandRequest.getBandName() + "%"));
        }

        if(bandRequest.getBandCountry() != null && !bandRequest.getBandCountry().isEmpty()){
            predicates.add(cb.like(band.get("bandInfo").get("country"),"%" + bandRequest.getBandCountry() + "%"));
        }

        if(bandRequest.getBandCity() != null && !bandRequest.getBandCity().isEmpty()){
            predicates.add(cb.like(band.get("bandInfo").get("city"),"%" + bandRequest.getBandCity() + "%"));
        }

        if (bandRequest.getBandGenres() != null && !bandRequest.getBandGenres().isEmpty()) {
            predicates.add(band.get("genres").get("name").in(bandRequest.getBandGenres()));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Band> query = entityManager.createQuery(cq);

        List<BandCreationDto> responseBand = new ArrayList<>();

        List<Band> bands = query.getResultList();

        bands.forEach(bandArray -> {
            responseBand.add(createBandResponseDto(bandArray));
        });

        return ResponseEntity.ok(responseBand);
    }

    private BandCreationDto createBandResponseDto(Band band) {
        return BandCreationDto.builder()
                .bandGenres(band.getGenres())
                .bandProfileImage(band.getImage())
                .bandInfo(band.getBandInfo())
                .bandContactInfo(band.getBandContactInfo())
                .leaderName(band.getMusicianLeader().getPersonalInformation().getName())
                .build();
    }
}
