package com.example.eventappbackend.Repositories;

import com.example.eventappbackend.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
        Optional<Token> findByToken(String token);
        }
