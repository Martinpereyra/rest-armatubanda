package com.restapi.armatubanda.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bands")
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "userSequenceGenerator")
    @GenericGenerator(
            name = "userSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "userSequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Column(name = "band_id")
    private int id;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "band_info_id")
    private BandInfo bandInfo;


    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "band_contact_info_id")
    private ContactInformation bandContactInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    private Musician musicianLeader;

    @ManyToMany
    private List<Genre> genres;

    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name = "band_image_id")
    private Image image;

    @ManyToMany
    private List<Musician> members;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Post> bandPosts;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Review> bandReviews;

}
