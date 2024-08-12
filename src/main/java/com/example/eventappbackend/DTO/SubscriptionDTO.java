package com.example.eventappbackend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDTO {
    private Long eventId;
    private String email;
}
