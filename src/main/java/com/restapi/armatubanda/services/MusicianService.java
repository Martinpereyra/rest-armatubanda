package com.restapi.armatubanda.services;

import com.restapi.armatubanda.dto.ProfileCreationDto;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicianService {
    private final MusicianRepository musicianRepository;

    private final InstrumentService instrumentService;

    public Optional<Musician> getMusician(String username){
        return musicianRepository.findByEmail(username);
    }


    public ResponseEntity<ProfileCreationDto> createProfile(
            Musician musicianToSave,
            MusicianContactInformation contactInformation,
            List<Instrument> instruments,
            Image image)
    {
        var musicianContactInformation = MusicianContactInformation.builder()
                .name(contactInformation.getName())
                .lastname(contactInformation.getLastname())
                .stageName(contactInformation.getStageName())
                .bio(contactInformation.getBio())
                .country(contactInformation.getCountry())
                .city(contactInformation.getCity())
                .phoneNumber(contactInformation.getPhoneNumber())
                .webSite(contactInformation.getWebSite())
                .socialMediaLink(contactInformation.getSocialMediaLink())
                .build();

        List<Instrument> instrumentsToSave = new ArrayList<>();

        for(Instrument instrument :instruments){
            instrumentsToSave.add(instrumentService.getInstrument(instrument.getName()).orElseThrow(()-> new UsernameNotFoundException("Instrument not found")));
        }
        musicianToSave.setMusicianContactInformation(musicianContactInformation);
        musicianToSave.setInstrument(instrumentsToSave);
        musicianToSave.setProfileSet(true);
        ProfileCreationDto fullProfile = new ProfileCreationDto();
        fullProfile.setMusicianContactInformation(musicianToSave.getMusicianContactInformation());
        fullProfile.setInstruments(musicianToSave.getInstrument());
        if(image != null) {
            musicianToSave.setImage(image);
            fullProfile.setProfileImage(musicianToSave.getImage());
        }
        musicianRepository.save(musicianToSave);
        return ResponseEntity.ok(fullProfile);
    }

    public Image uploadProfileImage(MultipartFile file) throws IOException {
        return Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .picByte(file.getBytes())
                .build();
    }

    public ResponseEntity<Musician> updateProfileImage(MultipartFile file) throws IOException {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Musician musician = getMusician(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        Image image = uploadProfileImage(file);
        musician.setImage(image);
        musicianRepository.save(musician);
        return ResponseEntity.ok(musician);
    }

    public ResponseEntity<List<Review>> uploadMusicianReview(Review review) {
        Musician musician = musicianRepository.findById(review.getMusicianId()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        List<Review> reviews = musician.getReviews();
        if (reviews.isEmpty()) {
            reviews = new ArrayList<>();
        }
        var newReview = Review.builder()
                .comment(review.getComment())
                .musicianId(review.getMusicianId())
                .reviewerId(review.getReviewerId())
                .build();
        reviews.add(newReview);
        musician.setReviews(reviews);
        return ResponseEntity.ok(reviews);
    }
}
