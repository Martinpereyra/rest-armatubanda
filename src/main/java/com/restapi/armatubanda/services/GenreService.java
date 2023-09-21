package com.restapi.armatubanda.services;

import com.restapi.armatubanda.model.Genre;
import com.restapi.armatubanda.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
}
