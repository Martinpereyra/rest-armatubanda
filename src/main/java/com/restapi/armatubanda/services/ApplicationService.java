package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.ApplicationRequestDto;
import com.restapi.armatubanda.dto.ApplicationResponseDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.BandAdvertisement;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.model.MusicianApplication;
import com.restapi.armatubanda.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AdvertisementService advertisementService;
    private final AuthenticationService authenticationService;
    private final BandService bandService;
    public HttpStatus createApplication(ApplicationRequestDto applicationRequestDto) {
        Musician musician = this.authenticationService.getMusicianLogged();
        BandAdvertisement bandAdvertisement = this.advertisementService.getAdvertisement(applicationRequestDto.getIdApplication());

        if(bandAdvertisement.getBand().getMembers().contains(musician)){
            throw new RuntimeException();
        }

        MusicianApplication musicianApplication = MusicianApplication.builder()
                .musician(musician)
                .advertisement(bandAdvertisement)
                .accepted(false)
                .message(applicationRequestDto.getMessage())
                .build();

        this.applicationRepository.save(musicianApplication);
        return HttpStatus.OK;

    }

    public List<ApplicationResponseDto> getApplicationsByBand(int adId) {
        Musician musicianLogged = this.authenticationService.getMusicianLogged();
        BandAdvertisement ad = this.advertisementService.getAdvertisement(adId);
        if (ad.getBand().getMusicianLeader().getId() != musicianLogged.getId()){
            throw new RuntimeException();
        }
        List<MusicianApplication> applicationList = this.applicationRepository.findAllByAd(adId);
        return this.convertApplicationDto(applicationList);

    }


    public List<ApplicationResponseDto> convertApplicationDto(List<MusicianApplication> applicationList){
        List<ApplicationResponseDto> responseList = new ArrayList<>();

        for(MusicianApplication application: applicationList){
            ApplicationResponseDto appResponse = ApplicationResponseDto.builder()
                    .applicationId(application.getId())
                    .applicationMessage(application.getMessage())
                    .musicianId(application.getMusician().getId())
                    .musicianImage(application.getMusician().getImage())
                    .musicianName(application.getMusician().getPersonalInformation().getStageName())
                    .build();

            responseList.add(appResponse);
        }
        return responseList;
    }

    public HttpStatus acceptApplication(int applicationId, boolean status) {
        MusicianApplication musicianApplication = this.applicationRepository.findById(applicationId).orElseThrow(()-> new UsernameNotFoundException("Application not found"));
        Musician musicianLogged = this.authenticationService.getMusicianLogged();
        Band band = musicianApplication.getAdvertisement().getBand();
        if (musicianLogged.getId() != band.getMusicianLeader().getId()){
            throw new RuntimeException();
        }
        if(status){
            Musician musician = musicianApplication.getMusician();
            if(this.bandService.addMember(musician,band)){
                this.applicationRepository.delete(musicianApplication);
                return HttpStatus.OK;
            }else {
                throw new RuntimeException();
            }
        }else{
            this.applicationRepository.delete(musicianApplication);
            return HttpStatus.ACCEPTED;
        }
    }

    public boolean applicationExists(int musicianId,int adId){
        return this.applicationRepository.existsByMusicianIdAndAdvertisementId(musicianId,adId);
    }
}
