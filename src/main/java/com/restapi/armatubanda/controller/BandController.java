package com.restapi.armatubanda.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bands")
public class BandController {


    @GetMapping("/all")
    public String getAllBands(){
        return "Todas las bandas";
    }

}
