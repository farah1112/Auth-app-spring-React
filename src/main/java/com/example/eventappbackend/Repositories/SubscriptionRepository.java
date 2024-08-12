package com.example.eventappbackend.Repositories;

import com.example.eventappbackend.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
