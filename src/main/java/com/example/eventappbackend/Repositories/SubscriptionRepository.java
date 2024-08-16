package com.example.eventappbackend.Repositories;

import com.example.eventappbackend.entities.Event;
import com.example.eventappbackend.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByEventAndEmail(Event event, String email);

}
