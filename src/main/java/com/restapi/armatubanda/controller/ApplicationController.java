package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.dto.ApplicationRequestDto;
import com.restapi.armatubanda.dto.ApplicationResponseDto;
import com.restapi.armatubanda.model.Musician;
import com.restapi.armatubanda.services.ApplicationService;
import com.restapi.armatubanda.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping()
    public HttpStatus createApplication(@RequestBody ApplicationRequestDto applicationRequestDto){
        return applicationService.createApplication(applicationRequestDto);
    }

    @GetMapping
    public List<ApplicationResponseDto> getApplicationsByAd(
            @RequestParam("adId") int adId
    ){
        return this.applicationService.getApplicationsByBand(adId);
    }

    @PutMapping(value = "/status")
    public HttpStatus changeStatus(@RequestParam("applicationId") int applicationId,
                                   @RequestParam("status") boolean status){

        this.applicationService.acceptApplication(applicationId,status);
        return HttpStatus.OK;
    }




}
