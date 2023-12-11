package com.restapi.armatubanda.services;

import com.restapi.armatubanda.dto.*;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.MusicianRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicianService {
    private final MusicianRepository musicianRepository;

    private final InstrumentService instrumentService;

    private final GenreService genreService;

    public Optional<Musician> getMusician(String username){
        return musicianRepository.findByEmail(username);
    }

    public Optional<Musician> getMusicianById(int id){
        return musicianRepository.findById(id);
    }


    public ResponseEntity<Musician> createProfile(
            Musician musicianToSave,
            PersonalInformation personalInformation,
            ContactInformation contactInformation,
            SkillsInformation skillsInformation,
            EducationInformation educationInformation,
            CareerInformation careerInformation,
            BiographyInformation biographyInformation,
            PreferenceInformation preferenceInformation,
            Image image)
    {
        var musicianPersonalInformation = PersonalInformation.builder()
                .name(personalInformation.getName())
                .lastname(personalInformation.getLastname())
                .stageName(personalInformation.getStageName())
                .birthday(personalInformation.getBirthday())
                .gender(personalInformation.getGender())
                .country(personalInformation.getCountry())
                .city(personalInformation.getCity())
                .build();

        var musicianContactInformation = ContactInformation.builder()
                .phoneNumber(contactInformation.getPhoneNumber())
                .webSite(contactInformation.getWebSite())
                .socialMedia(contactInformation.getSocialMedia())
                .build();

        var musicianEducationInformation = EducationInformation.builder()
                .educationHistory(educationInformation.getEducationHistory())
                .build();

        var musicianCareerInformation = CareerInformation.builder()
                .careerHistory(careerInformation.getCareerHistory())
                .build();

        var musicianBiographyInformation = BiographyInformation.builder()
                .bio(biographyInformation.getBio())
                .build();

        var musicianPreferenceInformation = PreferenceInformation.builder()
                .lookingBands(preferenceInformation.isLookingBands())
                .lookingMusician(preferenceInformation.isLookingMusician())
                .available(preferenceInformation.isAvailable())
                .build();

        List<InstrumentExperience> instrumentsList = skillsInformation.getInstrumentExperience();
        List<InstrumentExperience> musicianInstrumentList = new ArrayList<>();

        for (InstrumentExperience instrumentElement: instrumentsList) {

           Instrument musicianInstrument = instrumentService.getInstrument(instrumentElement.getInstrument().getName()).orElseThrow(()->new UsernameNotFoundException("Instrument not found"));
           InstrumentExperience musicianInstrumentExperience = new InstrumentExperience();
           musicianInstrumentExperience.setInstrument(musicianInstrument);
           musicianInstrumentExperience.setExperience(instrumentElement.getExperience());
           musicianInstrumentList.add(musicianInstrumentExperience);
        }

        List<Genre> genreList = skillsInformation.getGenres();
        List<Genre> musicianGenreList = new ArrayList<>();

        for(Genre genreElement : genreList){
            musicianGenreList.add(genreService.getGenre(genreElement.getName()).orElseThrow(()-> new UsernameNotFoundException("Genre not found")));
        }

        var musicianSkillInformation = SkillsInformation.builder()
                .instrumentExperience(musicianInstrumentList)
                .genres(musicianGenreList)
                .generalExperience(skillsInformation.getGeneralExperience())
                .build();

        musicianToSave.setPersonalInformation(musicianPersonalInformation);
        musicianToSave.setContactInformation(musicianContactInformation);
        musicianToSave.setSkillsInformation(musicianSkillInformation);
        musicianToSave.setEducationInformation(musicianEducationInformation);
        musicianToSave.setCareerInformation(musicianCareerInformation);
        musicianToSave.setBiographyInformation(musicianBiographyInformation);
        musicianToSave.setPreferenceInformation(musicianPreferenceInformation);
        musicianToSave.setProfileSet(true);

        if(image != null) {
            musicianToSave.setImage(image);
        }
        musicianRepository.save(musicianToSave);
        return ResponseEntity.ok(musicianToSave);
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
                .reviewerFirstName(reviewer.getPersonalInformation().getName())
                .reviewerLastName(reviewer.getPersonalInformation().getLastname())
                .reviewerProfileImage(reviewer.getImage())
                .build();
        reviews.add(newReview);
        musician.setReviews(reviews);
        musicianRepository.save(musician);
        return ResponseEntity.ok(reviews);
    }

    public ResponseEntity<List<MusicianResponseDto>> getMusiciansList(MusicianRequestDto request) {
        List<Musician> musicians;
        if (request.getName() == null && request.getCity() == null) {
            musicians = musicianRepository.findAll()
                    .stream()
                    .filter(Musician::isProfileSet)
                    .collect(Collectors.toList());
        } else {
            musicians = musicianRepository.findBy(request.getName(), request.getCity());
        }
        List<MusicianResponseDto> responseMusicians = new ArrayList<>();
        musicians.forEach(musician -> {
            responseMusicians.add(createMusicianResponseDto(musician));
        });
        return ResponseEntity.ok(responseMusicians);
    }

    public MusicianResponseDto getById(int id) {
        var musician = musicianRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Musician not found with ID: " + id));
        return createMusicianResponseDto(musician);
    }

    private MusicianResponseDto createMusicianResponseDto(Musician musician) {
        return MusicianResponseDto.builder()
                .id(musician.getId())
                .personalInformation(musician.getPersonalInformation())
                .contactInformation(musician.getContactInformation())
                .skillsInformation(musician.getSkillsInformation())
                .educationInformation(musician.getEducationInformation())
                .careerInformation(musician.getCareerInformation())
                .biographyInformation(musician.getBiographyInformation())
                .preferenceInformation(musician.getPreferenceInformation())
                .profileImage(musician.getImage())
                .reviews(musician.getReviews())
                .build();
    }

    public ResponseEntity<MusicianProfileResponseDto> getMusicianProfile(int id) {

        Musician musicianToFind = musicianRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("Musician not found with ID: " + id));

        var musicianProfile = MusicianProfileResponseDto.builder()
                .firstName(musicianToFind.getPersonalInformation().getName())
                .lastName(musicianToFind.getPersonalInformation().getLastname())
                .stageName(musicianToFind.getPersonalInformation().getStageName())
                .biography(musicianToFind.getBiographyInformation().getBio())
                .contactInformation(musicianToFind.getContactInformation())
                .reviews(musicianToFind.getReviews())
                .image(musicianToFind.getImage())
                .build();

        return ResponseEntity.ok(musicianProfile);

    }

    public ResponseEntity<MusicianInformationResponseDto> getMusicianInformation(int id) {
        Musician musicianToFind = musicianRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("Musician not found with ID: " + id));

        var musicianInformation = MusicianInformationResponseDto.builder()
                .careerInformation(musicianToFind.getCareerInformation())
                .educationInformation(musicianToFind.getEducationInformation())
                .preferenceInformation(musicianToFind.getPreferenceInformation())
                .skillsInformation(musicianToFind.getSkillsInformation())
                .build();
        return ResponseEntity.ok(musicianInformation);
    }

    public ResponseEntity<Post> createPost(PostDto postDto, Image imagenPost, int userId) {
        Musician musician = musicianRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("Musician not found with ID: " + userId));

        List<Post> postList = musician.getPosts();

        Post newPost;
        if(postDto.isUrlPost()){
            newPost = Post.builder()
                    .videoUrl(postDto.getUrlVideo())
                    .urlPost(postDto.isUrlPost())
                    .build();
        }
        else{
            newPost = Post.builder()
                    .imagen(imagenPost)
                    .urlPost(postDto.isUrlPost())
                    .build();

        }
        postList.add(newPost);

        musician.setPosts(postList);
        musicianRepository.save(musician);

        return ResponseEntity.ok(newPost);
    }

    public ResponseEntity<List<PostDto>> getPosts(int id) {
        Musician musician = musicianRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: "+id));
        List<Post> listPost = musician.getPosts();

        List<PostDto> returnPostList = new ArrayList<>();
        if(!listPost.isEmpty()){
        for(Post post : listPost){
            var postDto = PostDto.builder()
                    .urlVideo(post.getVideoUrl())
                    .image(post.getImagen())
                    .build();
            returnPostList.add(postDto);
        }
        return ResponseEntity.ok(returnPostList);
        } else{
           return ResponseEntity.ok(null);
        }
    }
}
