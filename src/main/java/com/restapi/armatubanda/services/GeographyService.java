package com.restapi.armatubanda.services;

import com.restapi.armatubanda.repository.GeographyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeographyService {

    private final GeographyRepository geographyRepository;


    public List<String> getCountries() {
        return this.geographyRepository.findAllCountries();
    }

    public List<String> getCities(String countryName) {
        return this.geographyRepository.findAllCitiesByCountry(countryName);
    }
}
