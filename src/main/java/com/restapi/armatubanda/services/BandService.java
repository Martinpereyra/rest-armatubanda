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


    public ResponseEntity<BandCreationDto> createBand(BandCreationDto bandCreationDto, Musician bandLeader, MultipartFile file) throws IOException {

        List<Genre> genreList = bandCreationDto.getBandGenres();
        List<Genre> bandGenreList = new ArrayList<>();

        for(Genre genreElement : genreList){
            bandGenreList.add(genreService.getGenre(genreElement.getName()).orElseThrow(()-> new UsernameNotFoundException("Genre not found")));
        }



        var bandToSave = Band.builder()
                .bandInfo(bandCreationDto.getBandInfo())
                .bandContactInfo(bandCreationDto.getBandContactInfo())
                .musicianLeader(bandLeader)
                .genres(bandGenreList)
                .build();

        if (file != null){
            Image image = this.uploadProfileImage(file);
            bandToSave.setImage(image);
        }


        bandRepository.save(bandToSave);

        var responseBand = BandCreationDto.builder()
                .bandInfo(bandToSave.getBandInfo())
                .bandContactInfo(bandToSave.getBandContactInfo())
                .leaderName(bandToSave.getMusicianLeader().getPersonalInformation().getName())
                .bandGenres(bandToSave.getGenres())
                .bandProfileImage(bandToSave.getImage())
                .build();

        return ResponseEntity.ok(responseBand);

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
}
