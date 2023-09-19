package com.restapi.armatubanda.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum Experience {

    NOVICE("NOVICE"),
    ADVANCED_BEGINNER("ADVANCED BEGINNER"),
    ADVANCED("ADVANCED"),
    PROFICIENT("PROFICIENT"),
    EXPERT("EXPERT");

    private final String experience;
}