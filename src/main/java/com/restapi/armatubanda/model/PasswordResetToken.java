package com.restapi.armatubanda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false,name = "musician_id")
    private Musician musician;

    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public PasswordResetToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Musician musician){
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.musician = musician;
    }
}
