package com.restapi.armatubanda.repository;

import com.restapi.armatubanda.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Integer> {
    Optional<ConfirmationToken> findByConfirmationToken(String token);
}
