package com.restapi.armatubanda.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BandController {


    @GetMapping("/bands")
    public String getAllBands(){
        return "Todas las bandas";
    }

}
