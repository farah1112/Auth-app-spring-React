package com.example.eventappbackend.Repositories;

import com.example.eventappbackend.entities.Event;
import com.example.eventappbackend.entities.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(EventCategory category);

}