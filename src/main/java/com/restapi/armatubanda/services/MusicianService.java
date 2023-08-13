package com.restapi.armatubanda.services;

import com.restapi.armatubanda.dto.MusicianRequestDto;
import com.restapi.armatubanda.dto.MusicianResponseDto;
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
import java.util.stream.Collectors;

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
        musicianToSave.setInstruments(instrumentsToSave);
        musicianToSave.setProfileSet(true);
        ProfileCreationDto fullProfile = new ProfileCreationDto();
        fullProfile.setMusicianContactInformation(musicianToSave.getMusicianContactInformation());
        fullProfile.setInstruments(musicianToSave.getInstruments());
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

    public ResponseEntity<List<Review>> uploadMusicianReview(Review review) throws Exception {
        Musician musician = musicianRepository.findById(review.getMusicianId()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        Musician reviewer = musicianRepository.findById(review.getReviewerId()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if (!reviewer.isProfileSet() || !musician.isProfileSet()) {
            throw new Exception("Profile is not set");
        }
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
        musicianRepository.save(musician);
        return ResponseEntity.ok(reviews);
    }

    public ResponseEntity<List<MusicianResponseDto>> getMusiciansList(MusicianRequestDto request) {
        List<Musician> musicians;
        if (request.getName() == null && request.getCity() == null && request.getInstruments() == null) {
            musicians = musicianRepository.findAll()
                    .stream()
                    .filter(Musician::isProfileSet)
                    .collect(Collectors.toList());
        } else {
            musicians = musicianRepository.findBy(request.getName(), request.getCity(), request.getInstruments());
        }
        List<MusicianResponseDto> responseMusicians = new ArrayList<>();
        musicians.forEach(musician -> {
            var responseMusician = MusicianResponseDto.builder()
                    .id(musician.getId())
                    .musicianContactInformation(musician.getMusicianContactInformation())
                    .instruments(musician.getInstruments())
                    .profileImage(musician.getImage())
                    .reviews(musician.getReviews())
                    .build();
            responseMusicians.add(responseMusician);
        });
        return ResponseEntity.ok(responseMusicians);
    }
}
