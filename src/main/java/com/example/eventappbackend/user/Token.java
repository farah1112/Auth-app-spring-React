package com.example.eventappbackend.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue()
    private long id;
    private String token;
    private LocalDateTime createdat;
    private LocalDateTime expiredat;
    private LocalDateTime validatedat;

    @ManyToOne
    @JoinColumn(name = "user_Id",nullable = false)
    private User user;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
