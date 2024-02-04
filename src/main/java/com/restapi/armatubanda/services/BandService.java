package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.BandCreationDto;
import com.restapi.armatubanda.dto.BandMembersDto;
import com.restapi.armatubanda.dto.BandRequestDto;
import com.restapi.armatubanda.model.*;
import com.restapi.armatubanda.repository.BandRepository;
import com.restapi.armatubanda.repository.MusicianRepository;
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
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepository;

    private final GenreService genreService;

    private final EntityManager entityManager;

    private final AuthenticationService authenticationService;

    private final MusicianRepository musicianRepository;


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

        bandCreationDto.setBandId(band.getId());
        bandCreationDto.setBandInfo(band.getBandInfo());
        bandCreationDto.setBandContactInfo(band.getBandContactInfo());
        if (band.getMusicianLeader() != null && band.getMusicianLeader().getPersonalInformation() != null) {
            BandMembersDto leader = BandMembersDto.builder()
                    .musicianId(band.getMusicianLeader().getId())
                    .musicianName(band.getMusicianLeader().getPersonalInformation().getName())
                    .musicianLastName(band.getMusicianLeader().getPersonalInformation().getLastname())
                    .musicianProfileImage(band.getMusicianLeader().getImage())
                    .build();
            bandCreationDto.setLeader(leader);
        }
        bandCreationDto.setBandProfileImage(band.getImage());
        bandCreationDto.setBandGenres(genreService.getGenreStringList(band.getGenres()));
        if(band.getBandPosts() != null){
        bandCreationDto.setPostList(band.getBandPosts());
        }
        if(band.getMembers() != null){
        bandCreationDto.setBandMembersList(convertToBandMembersDtoList(band.getMembers()));
        }
        if(band.getBandReviews() != null){
            bandCreationDto.setReviewsList(band.getBandReviews());
        }
        return bandCreationDto;
    }

    public BandMembersDto convertToBandMembersDto(Musician member){
        return BandMembersDto.builder()
                .musicianId(member.getId())
                .musicianName(member.getPersonalInformation().getName())
                .musicianLastName(member.getPersonalInformation().getLastname())
                .musicianProfileImage(member.getImage())
                .build();
    }

    public List<BandMembersDto> convertToBandMembersDtoList(List<Musician> members){
        List<BandMembersDto> membersDtoList = new ArrayList<>();

        for(Musician musician : members){
            BandMembersDto musicianDto = convertToBandMembersDto(musician);
            membersDtoList.add(musicianDto);
        }
        return membersDtoList;
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
                .bandId(band.getId())
                .bandGenres(genreService.getGenreStringList(band.getGenres()))
                .bandProfileImage(band.getImage())
                .bandInfo(band.getBandInfo())
                .bandContactInfo(band.getBandContactInfo())
                .leader(convertToBandMembersDto(band.getMusicianLeader()))
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

    public BandCreationDto getBand(int bandId) {
        Band band = this.bandRepository.findById(bandId).orElseThrow(() -> new UsernameNotFoundException("Band not found"));
        return convertToBandCreationDto(band);
    }

    public ResponseEntity<Post> createPost(String videoUrl, MultipartFile file, int bandId) throws IOException {
        Band band = bandRepository.findById(bandId).orElseThrow(()->
                new UsernameNotFoundException("Band not found with ID: " + bandId));

        Musician musicianLogged = this.authenticationService.getMusicianLogged();

        if(band.getMusicianLeader().getId() != musicianLogged.getId()){
            throw new RuntimeException();
        }
        List<Post> posts = band.getBandPosts();

        Image image = null;
        Post newPost = new Post();
        if(file != null) {
            image = uploadProfileImage(file);
            newPost.setImage(image);
            posts.add(newPost);
        }else{
            if(videoUrl != null && !videoUrl.isEmpty()){
                newPost.setVideoUrl(videoUrl);
                posts.add(newPost);
            }
        }
        band.setBandPosts(posts);
        this.bandRepository.save(band);

        return ResponseEntity.ok(newPost);
    }

    public ResponseEntity<List<Post>> getPosts(int bandId) {
        Band band = this.bandRepository.findById(bandId).orElseThrow(()-> new UsernameNotFoundException("Band not found with id: "+bandId));
        List<Post> bandPost = band.getBandPosts();
        bandPost.sort(Comparator.comparing(Post::getCreatedOn).reversed());
        return ResponseEntity.ok(bandPost);

    }

    public ResponseEntity<List<Review>> uploadMusicianReview(Review review) {
        Band band = this.bandRepository.findById(review.getMusicianId()).orElseThrow(()-> new UsernameNotFoundException("Band not found with id "+review.getMusicianId()));
        Musician reviewer = this.musicianRepository.findById(review.getReviewerId()).orElseThrow(()-> new UsernameNotFoundException("Musician not found with id: "+review.getReviewerId()));

        List<Review> bandReviews = band.getBandReviews();

        if(bandReviews.isEmpty()){
            bandReviews = new ArrayList<>();
        }
        var newReview = Review.builder()
                .comment(review.getComment())
                .reviewerId(review.getReviewerId())
                .reviewerFirstName(reviewer.getPersonalInformation().getName())
                .reviewerLastName(reviewer.getPersonalInformation().getLastname())
                .reviewerProfileImage(reviewer.getImage())
                .build();
        bandReviews.add(newReview);
        band.setBandReviews(bandReviews);
        this.bandRepository.save(band);
        return ResponseEntity.ok(bandReviews);
    }
}
