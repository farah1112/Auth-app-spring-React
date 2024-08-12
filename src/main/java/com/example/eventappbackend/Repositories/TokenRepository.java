package com.example.eventappbackend.Repositories;

import com.example.eventappbackend.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
        Optional<Token> findByToken(String token);
        void deleteByUserId(Long userId);

    List<Token> findByUserId(Long id);
}
