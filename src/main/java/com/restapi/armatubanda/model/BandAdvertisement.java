package com.restapi.armatubanda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandAdvertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "band_id")
    private Band band;

    @ManyToMany
    private List<Genre> genres;

    @CreationTimestamp
    private Instant createdOn;
}
