package com.restapi.armatubanda.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum Gender {
    MALE("male"),
    FEMALE("female"),
    GENDERLESS("genderless");

    private final String gender;

}