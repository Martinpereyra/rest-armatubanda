package com.restapi.armatubanda.services;

import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public ResponseEntity<Genre> create(String genreName) {
        Genre genre = Genre.builder().name(genreName).build();
        return ResponseEntity.ok(genreRepository.save(genre));
    }

    public List<Genre> getAll() { return genreRepository.findAll(); }

    public ResponseEntity<Genre> update(Genre updatedInstrument) {
        Optional<Genre> genre = genreRepository.findById(updatedInstrument.getId());
        if (genre.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Genre existingInstrument = Genre.builder()
                .id(updatedInstrument.getId())
                .name(updatedInstrument.getName())
                .build();
        Genre savedInstrument = genreRepository.save(existingInstrument);
        return ResponseEntity.ok(savedInstrument);
    }

    public String delete(int id) {
        if (genreRepository.existsById(id)) {
            genreRepository.deleteById(id);
            return "Género eliminado correctamente";
        } else {
            return "El género con ID " + id + " no existe";
        }
    }

    public Optional<Genre> getGenre(String name){return genreRepository.findGenreByName(name);}

    public List<Genre> getGenreList(List<Genre> genres){
        List<Genre> musicianGenreList = new ArrayList<>();
        for(Genre genreElement : genres){
            musicianGenreList.add(this.getGenre(genreElement.getName()).orElseThrow(()-> new UsernameNotFoundException("Genre not found")));
        }
        return musicianGenreList;
    }

    public List<Genre> getGenreListString(List<String> genresNames) throws UsernameNotFoundException{
        List<Genre> responseList = new ArrayList<>();

        for(String genreName:genresNames){
            responseList.add(this.genreRepository.findGenreByName(genreName).orElseThrow(()-> new UsernameNotFoundException("Genre not found")));
        }

        return responseList;
    }
}
