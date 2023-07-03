package com.restapi.armatubanda.controller;

import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/genre")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200",maxAge = 3600)
public class GenreController {

    private final GenreService genreService;

    @PostMapping("/add/{name}")
    public ResponseEntity<Genre> create(@PathVariable("name") String genreName){
        return genreService.create(genreName);
    }

    @GetMapping()
    public List<Genre> getAll() {
        return genreService.getAll();
    }

    @PutMapping()
    public ResponseEntity<Genre> update(@RequestBody @Valid Genre genre) {
        return genreService.update(genre);
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable("id") int id) {
        return genreService.delete(id);
    }
}
