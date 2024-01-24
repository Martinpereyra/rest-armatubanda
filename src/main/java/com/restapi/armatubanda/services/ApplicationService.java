package com.restapi.armatubanda.services;


import com.restapi.armatubanda.dto.ApplicationRequestDto;
import com.restapi.armatubanda.dto.ApplicationResponseDto;
import com.restapi.armatubanda.model.BandAdvertisement;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.model.MusicianApplication;
import com.restapi.armatubanda.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AdvertisementService advertisementService;
    private final AuthenticationService authenticationService;
    public HttpStatus createApplication(ApplicationRequestDto applicationRequestDto) {
        Musician musician = this.authenticationService.getMusicianLogged();
        BandAdvertisement bandAdvertisement = this.advertisementService.getAdvertisement(applicationRequestDto.getIdApplication());

        MusicianApplication musicianApplication = MusicianApplication.builder()
                .musician(musician)
                .bandAdvertisement(bandAdvertisement)
                .accepted(false)
                .message(applicationRequestDto.getMessage())
                .build();

        this.applicationRepository.save(musicianApplication);
        return HttpStatus.OK;

    }

    public List<ApplicationResponseDto> getApplicationsByBand(int bandId) {
        List<MusicianApplication> applicationList = this.applicationRepository.findAllByBand(bandId);
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
}
