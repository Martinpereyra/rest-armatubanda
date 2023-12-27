package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.BandCreationDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.model.Image;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.repository.BandRepository;
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
}
