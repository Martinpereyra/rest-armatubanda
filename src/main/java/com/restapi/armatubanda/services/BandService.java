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

        List<Genre> genreList = genreService.getGenreListString(bandCreationDto.getBandGenres());
        
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
        bandCreationDto.setBandGenres(genreService.getGenreStringList(band.getGenres()));

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

    public ResponseEntity<List<BandCreationDto>> getBandList(
            String name,
            String country,
            String city,
            List<String> genres
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Band> cq = cb.createQuery(Band.class);
        Root<Band> band = cq.from(Band.class);

        List<Predicate> predicates = new ArrayList<>();

        if(name != null && !name.isEmpty()){
            predicates.add(cb.like(band.get("bandInfo").get("name"),"%" + name + "%"));
        }

        if(country != null && !country.isEmpty()){
            predicates.add(cb.like(band.get("bandInfo").get("country"),"%" + country + "%"));
        }

        if(city != null && !city.isEmpty()){
            predicates.add(cb.like(band.get("bandInfo").get("city"),"%" + city + "%"));
        }

        if (genres != null && !genres.isEmpty()) {
            predicates.add(band.get("genres").get("name").in(genres));
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
                .bandGenres(genreService.getGenreStringList(band.getGenres()))
                .bandProfileImage(band.getImage())
                .bandInfo(band.getBandInfo())
                .bandContactInfo(band.getBandContactInfo())
                .leaderName(band.getMusicianLeader().getPersonalInformation().getName())
                .build();
    }

    public boolean addMember(Musician musician, Band band){
        try{
        List<Musician> updateMembersList = band.getMembers();
        updateMembersList.add(musician);
        band.setMembers(updateMembersList);
        this.bandRepository.save(band);
        return true;
        }catch (Exception e){
            return false;
        }
    }
}
