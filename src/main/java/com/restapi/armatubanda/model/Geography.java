package com.restapi.armatubanda.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Geography {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String countryCode;
    private String countryName;
    private String cityName;


}
