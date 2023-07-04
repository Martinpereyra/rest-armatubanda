package com.restapi.armatubanda.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "musicians")
public class Musician implements UserDetails {

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
    @Column(name = "musician_id")
    private int id;

    @Email
    @NaturalId(mutable = true)
    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isProfileSet;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "musician_contact_information_id")
    private MusicianContactInformation musicianContactInformation;

    @ElementCollection
    @CollectionTable(
            name="musician_instrument_table",
            joinColumns = @JoinColumn(name = "musician_id")
    )
    private List<Instrument> instrument;

    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(name = "musician_image",
        joinColumns = {
            @JoinColumn(name = "musician_id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "image_id")
        }
    )
    private Image image;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
