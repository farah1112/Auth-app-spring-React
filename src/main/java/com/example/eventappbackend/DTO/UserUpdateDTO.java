package com.example.eventappbackend.DTO;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class UserUpdateDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate dateNaissance;
    private boolean accountlocked;
    private boolean enabled;
    private List<String> roles;
}
