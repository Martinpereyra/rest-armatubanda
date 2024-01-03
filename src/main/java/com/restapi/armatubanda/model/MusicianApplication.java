package com.restapi.armatubanda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicianApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "advertisement_id")
    private BandAdvertisement bandAdvertisement;

    @ManyToOne
    @JoinColumn(name = "musician_id")
    private Musician musician;

    @CreationTimestamp
    private Instant createdOn;

    private Boolean accepted;


}
