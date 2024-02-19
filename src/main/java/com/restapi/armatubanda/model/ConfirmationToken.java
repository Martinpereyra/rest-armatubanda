package com.restapi.armatubanda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String confirmationToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private Musician user;

    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Musician musician) {
        this.confirmationToken = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.user = musician;
    }

}
