package com.example.eventappbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    private String location;
    private String photo;
    private Integer rating;


}
