package com.restapi.armatubanda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SkillsInformation {
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



    @OneToMany(cascade=CascadeType.ALL)
    private List<InstrumentExperience> instrumentExperience;

    @ElementCollection
    @CollectionTable(
            name="musician_genres",
            joinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Genre> genres;

    @Enumerated(EnumType.STRING)
    private Experience generalExperience;


}
