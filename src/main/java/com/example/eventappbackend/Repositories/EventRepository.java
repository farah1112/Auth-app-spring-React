package com.example.eventappbackend.Repositories;

import com.example.eventappbackend.entities.Event;
import com.example.eventappbackend.entities.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(EventCategory category);
    //recent ones
    @Query("SELECT e FROM Event e WHERE e.category = :category AND e.endDate >= :today")
    List<Event> findEventsByCategoryAndDate(@Param("category") EventCategory category, @Param("today") LocalDateTime today);

    @Query("SELECT e FROM Event e WHERE e.endDate >= CURRENT_TIMESTAMP ORDER BY e.id DESC")
    List<Event> findLatestEvents(Pageable pageable);
}