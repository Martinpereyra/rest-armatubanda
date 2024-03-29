package com.restapi.armatubanda.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"musician_id", "band_id"})
})
public class Invitation {

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
    @Column(name = "invitation_id")
    private int id;


    @ManyToOne
    @JoinColumn(name = "musician_id")
    private Musician musicianInvited;

    @ManyToOne
    @JoinColumn(name = "band_id")
    private Band bandInvitation;

    private boolean status;

}
