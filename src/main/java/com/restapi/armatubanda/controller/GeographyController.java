package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.services.GeographyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class GeographyController {
    private final GeographyService geographyService;


    @GetMapping(value = "/countries")
    public List<String> getCountries(){
        return this.geographyService.getCountries();
    }


    @GetMapping(value = "/cities")
    public List<String> getCities(@RequestParam("countryName") String countryName){
        return this.geographyService.getCities(countryName);
    }

}
