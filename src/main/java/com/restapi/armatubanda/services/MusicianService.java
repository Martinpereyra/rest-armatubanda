package com.restapi.armatubanda.services;

import com.restapi.armatubanda.dto.*;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.MusicianRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MusicianService {
    private final MusicianRepository musicianRepository;

    private final InstrumentService instrumentService;

    private final GenreService genreService;

    private final EntityManager entityManager;

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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Musician> cq = cb.createQuery(Musician.class);
        Root<Musician> musician = cq.from(Musician.class);
        Join<Musician, SkillsInformation> skills = musician.join("skillsInformation", JoinType.LEFT);
        Join<SkillsInformation, Genre> genres = skills.join("genres", JoinType.LEFT);
        Join<SkillsInformation, InstrumentExperience> instrumentExperience = skills.join("instrumentExperience", JoinType.LEFT);
        Join<InstrumentExperience, Instrument> instruments = instrumentExperience.join("instrument", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (request.getName() != null && !request.getName().isEmpty()) {
            predicates.add(cb.like(musician.get("personalInformation").get("name"), "%" + request.getName() + "%"));
        }
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            predicates.add(cb.like(musician.get("personalInformation").get("city"), "%" + request.getCity() + "%"));
        }
        if(request.getCountry() != null && !request.getCountry().isEmpty()){
            predicates.add(cb.like(musician.get("personalInformation").get("country"),"%" + request.getCountry() + "%"));
        }
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            predicates.add(genres.get("name").in(request.getGenres()));
        }
        if (request.getInstruments() != null && !request.getInstruments().isEmpty()) {
            predicates.add(instruments.get("name").in(request.getInstruments()));
        }
        if (request.getExperience() != null && !request.getExperience().isEmpty()) {
            predicates.add(cb.equal(skills.get("generalExperience"), Experience.valueOf(request.getExperience())));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Musician> query = entityManager.createQuery(cq);

        List<MusicianResponseDto> responseMusicians = new ArrayList<>();

        List<Musician> musicians = query.getResultList();

        musicians.forEach(musicianArray -> {
            responseMusicians.add(createMusicianResponseDto(musicianArray));
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

    public ResponseEntity<Post> createPost(String videoUrl, MultipartFile file, int userId) throws IOException {
        Musician musician = musicianRepository.findById(userId).orElseThrow(()->
                new UsernameNotFoundException("Musician not found with ID: " + userId));

        Image image = null;
        if(file != null) {
            image = uploadProfileImage(file);
        }

        List<Post> posts = musician.getPosts();
        Post newPost = Post.builder()
                .videoUrl(videoUrl)
                .image(image)
                .build();
        posts.add(newPost);
        musician.setPosts(posts);
        musicianRepository.save(musician);

        return ResponseEntity.ok(newPost);
    }

    public ResponseEntity<List<PostDto>> getPosts(int id) {
        Musician musician = musicianRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: "+id));
        List<Post> listPost = musician.getPosts();
        List<PostDto> posts = new ArrayList<>();

        for(Post post : listPost){
            var postDto = PostDto.builder()
                    .Id(post.getId())
                    .urlVideo(post.getVideoUrl())
                    .image(post.getImage())
                    .createdOn(post.getCreatedOn())
                    .build();
            posts.add(postDto);
        }
        posts.sort(Comparator.comparing(PostDto::getCreatedOn).reversed());

        return ResponseEntity.ok(posts);
    }
}
