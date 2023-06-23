package com.restapi.armatubanda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MusicianContactInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "userSequenceGenerator")
    @GenericGenerator(
            name = "userSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "userSequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @JsonIgnore
    private int id;
    private String name;
    private String lastname;
    private String country;
    private String city;
    private String phoneNumber;

    private String webSite;

    private String socialMediaLink;


}
