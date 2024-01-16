package com.restapi.armatubanda.services;

import com.restapi.armatubanda.dto.*;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.BandRepository;
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

    private final BandRepository bandRepository;

    private final EntityManager entityManager;


    public Optional<Musician> getMusician(String username){
        return musicianRepository.findByEmail(username);
    }

    public Optional<Musician> getMusicianById(int id){
        return musicianRepository.findById(id);
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

    public ResponseEntity<List<MusicianResponseDto>> getMusiciansList(
            String name,
            String city,
            String country,
            List<String> musicianGenres,
            List<String> musicianInstruments,
            String experience,
            Boolean lookingBand
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Musician> cq = cb.createQuery(Musician.class);
        Root<Musician> musician = cq.from(Musician.class);
        Join<Musician, SkillsInformation> skills = musician.join("skillsInformation", JoinType.LEFT);
        Join<SkillsInformation, Genre> genres = skills.join("genres", JoinType.LEFT);
        Join<SkillsInformation, InstrumentExperience> instrumentExperience = skills.join("instrumentExperience", JoinType.LEFT);
        Join<InstrumentExperience, Instrument> instruments = instrumentExperience.join("instrument", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(musician.get("personalInformation").get("name"), "%" + name + "%"));
        }
        if (city != null && !city.isEmpty()) {
            predicates.add(cb.like(musician.get("personalInformation").get("city"), "%" + city + "%"));
        }
        if(country != null && !country.isEmpty()){
            predicates.add(cb.like(musician.get("personalInformation").get("country"),"%" + country + "%"));
        }
        if (musicianGenres != null && !musicianGenres.isEmpty()) {
            predicates.add(genres.get("name").in(musicianGenres));
        }
        if (musicianInstruments != null && !musicianInstruments.isEmpty()) {
            predicates.add(instruments.get("name").in(musicianInstruments));
        }
        if (experience != null && !experience.isEmpty()) {
            predicates.add(cb.equal(skills.get("generalExperience"), Experience.valueOf(experience)));
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

    public MusicianResponseDto createProfileAlt(Musician musicianToSave,ProfileCreationDto profileInfoDto, MultipartFile file) throws IOException {

        if (musicianToSave.isProfileSet()) {
            throw new IllegalStateException("Profile already set");
        }

        Image image = null;
        if (file != null && !file.isEmpty()) {
            image = uploadProfileImage(file);
        }

        return createMusicianProfile(musicianToSave, profileInfoDto, image);

    }

    private MusicianResponseDto createMusicianProfile(Musician musicianToSave, ProfileCreationDto profileInfoDto, Image image) {
        updatePersonalInformation(musicianToSave, profileInfoDto.getPersonalInformation());

        updateContactInformation(musicianToSave, profileInfoDto.getContactInformation());

        updateSkillsInformation(musicianToSave, profileInfoDto.getSkillsInformation());

        updateEducationInformation(musicianToSave, profileInfoDto.getEducationInformation());

        updateCareerInformation(musicianToSave, profileInfoDto.getCareerInformation());

        updateBiographyInformation(musicianToSave, profileInfoDto.getBiographyInformation());

        updatePreferenceInformation(musicianToSave, profileInfoDto.getPreferenceInformation());

        if (image != null) {
            musicianToSave.setImage(image);
        }

        musicianToSave.setProfileSet(true);

        Musician updatedMusician = musicianRepository.save(musicianToSave);

        return convertToMusicianResponseDto(updatedMusician);
    }

    private MusicianResponseDto convertToMusicianResponseDto(Musician musician) {
        MusicianResponseDto dto = new MusicianResponseDto();
        dto.setPersonalInformation(musician.getPersonalInformation());
        dto.setContactInformation(musician.getContactInformation());
        dto.setSkillsInformation(musician.getSkillsInformation());
        dto.setEducationInformation(musician.getEducationInformation());
        dto.setCareerInformation(musician.getCareerInformation());
        dto.setBiographyInformation(musician.getBiographyInformation());
        dto.setPreferenceInformation(musician.getPreferenceInformation());
        dto.setProfileImage(musician.getImage());
        dto.setReviews(musician.getReviews());

        return dto;
    }

    private void updatePreferenceInformation(Musician musicianToSave, PreferenceInformation preferenceInformation) {
        if(preferenceInformation != null){
            musicianToSave.setPreferenceInformation(preferenceInformation);
        }
    }

    private void updateBiographyInformation(Musician musicianToSave, BiographyInformation biographyInformation) {
        if(biographyInformation != null){
            musicianToSave.setBiographyInformation(biographyInformation);
        }
    }

    private void updateCareerInformation(Musician musicianToSave, CareerInformation careerInformation) {
        if(careerInformation != null){
            musicianToSave.setCareerInformation(careerInformation);
        }
    }

    private void updateEducationInformation(Musician musicianToSave, EducationInformation educationInformation) {
        if(educationInformation != null) {
            musicianToSave.setEducationInformation(educationInformation);
        }
        
    }

    private void updateSkillsInformation(Musician musicianToSave, SkillsInformation skillsInformation) {
        if(skillsInformation != null){
            skillsInformation.setInstrumentExperience(updateInstrumentExperience(skillsInformation.getInstrumentExperience()));
            skillsInformation.setGenres(updateGenres(skillsInformation.getGenres()));
            musicianToSave.setSkillsInformation(skillsInformation);
        }
    }

    private void updateContactInformation(Musician musicianToSave, ContactInformation contactInformation) {
        if (contactInformation != null){
            musicianToSave.setContactInformation(contactInformation);
        }
    }

    private void updatePersonalInformation(Musician musicianToSave, PersonalInformation personalInformation) {
        if (personalInformation != null) {
            musicianToSave.setPersonalInformation(personalInformation);
        }
    }

    private List<InstrumentExperience> updateInstrumentExperience(List<InstrumentExperience> instrumentExperience){

        List<InstrumentExperience> musicianInstrumentList = new ArrayList<>();
        for (InstrumentExperience instrumentElement: instrumentExperience) {

            Instrument musicianInstrument = instrumentService.getInstrument(instrumentElement.getInstrument().getName()).orElseThrow(()->new UsernameNotFoundException("Instrument not found"));
            InstrumentExperience musicianInstrumentExperience = new InstrumentExperience();
            musicianInstrumentExperience.setInstrument(musicianInstrument);
            musicianInstrumentExperience.setExperience(instrumentElement.getExperience());
            musicianInstrumentList.add(musicianInstrumentExperience);
        }
        return musicianInstrumentList;
    }

    private List<Genre> updateGenres(List<Genre> genres){
        List<Genre> musicianGenreList = new ArrayList<>();
        for(Genre genreElement : genres){
            musicianGenreList.add(genreService.getGenre(genreElement.getName()).orElseThrow(()-> new UsernameNotFoundException("Genre not found")));
        }
        return musicianGenreList;
    }

    public ResponseEntity<List<MusicianBandsDto>> getMusicianBands(int musicianId) {

        List<Band> musicianBands = bandRepository.findAlLByMembers_Id(musicianId);

        List<MusicianBandsDto> responseMusicianBands = new ArrayList<>();

        for(Band band : musicianBands){

            MusicianBandsDto bandDto = MusicianBandsDto.builder()
                    .bandId(band.getId())
                    .bandName(band.getBandInfo().getName())
                    .bandImage(band.getImage())
                    .build();

            responseMusicianBands.add(bandDto);
        }

        // TODO: Agregar bandas que es lider

        return ResponseEntity.ok(responseMusicianBands);

    }

    public ResponseEntity<List<MusicianBandsDto>> getMusicianLeaderBands(int musicianId){

        List<Band> musicianLeaderBands = bandRepository.findByMusicianLeaderId(musicianId);
        List<MusicianBandsDto> musicianLeaderBandsDto = new ArrayList<>();

        for (Band band : musicianLeaderBands){
            MusicianBandsDto bandDto = MusicianBandsDto.builder()
                    .bandId(band.getId())
                    .bandName(band.getBandInfo().getName())
                    .bandImage(band.getImage())
                    .build();

            musicianLeaderBandsDto.add(bandDto);
        }

        return ResponseEntity.ok(musicianLeaderBandsDto);


    }
}