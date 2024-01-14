package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.AdvertisementRequestDto;
import com.restapi.armatubanda.model.Band;
import com.restapi.armatubanda.model.BandAdvertisement;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.services.AdvertisementService;
import com.restapi.armatubanda.services.AuthenticationService;
import com.restapi.armatubanda.services.BandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ad")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    private final AuthenticationService authenticationService;

    private final BandService bandService;

    @PostMapping(value = "/create")
    public ResponseEntity<BandAdvertisement> createAd(@RequestBody AdvertisementRequestDto advertisementRequestDto) throws Exception {

        try{
            Musician bandLeader = authenticationService.getMusicianLogged();
            Band band = bandService.getBandById(advertisementRequestDto.getBandId());
            return advertisementService.createAd(bandLeader,band,advertisementRequestDto);

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @DeleteMapping(value = "/delete/{adId}")
    public HttpStatus deleteAd(@PathVariable int adId) throws Exception {
        Musician bandLeader = authenticationService.getMusicianLogged();
        return advertisementService.deleteAd(bandLeader,adId);
    }




}
