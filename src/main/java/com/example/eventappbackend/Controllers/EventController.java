package com.example.eventappbackend.Controllers;

import com.example.eventappbackend.DTO.EventDTO;
import com.example.eventappbackend.entities.Event;
import com.example.eventappbackend.entities.EventCategory;
import com.example.eventappbackend.entities.Subscription;
import com.example.eventappbackend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/event/add")
    public ResponseEntity<String> addEvent(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String location,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {

        if (title == null || category == null) {
            return ResponseEntity.badRequest().body("Title and category are required.");
        }

        Event event = new Event();
        event.setTitle(title);
        event.setCategory(EventCategory.valueOf(category.toUpperCase()));
        event.setDescription(description);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setLocation(location);

        if (photo != null && !photo.isEmpty()) {
            String photoPath = eventService.uploadPhoto(photo);
            event.setPhoto(photoPath);
        }

        eventService.addEvent(event);
        return ResponseEntity.ok("Event added successfully");
    }
    @PutMapping("/event/{id}")
    public ResponseEntity<String> updateEvent(
            @PathVariable Long id,
            @ModelAttribute EventDTO eventDTO,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {

        Event existingEvent = eventService.getEvent(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));
        existingEvent.setTitle(eventDTO.getTitle());
        existingEvent.setCategory(EventCategory.valueOf(eventDTO.getCategory().toUpperCase()));
        existingEvent.setDescription(eventDTO.getDescription());
        existingEvent.setStartDate(eventDTO.getStartDate());
        existingEvent.setEndDate(eventDTO.getEndDate());
        existingEvent.setLocation(eventDTO.getLocation());

        if (photo != null && !photo.isEmpty()) {
            String photoPath = eventService.uploadPhoto(photo);
            existingEvent.setPhoto(photoPath);
        }

        eventService.updateEvent(existingEvent);
        return ResponseEntity.ok("Event updated successfully");
    }

    @GetMapping("/event/{id}")
    public Optional<Event> getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping("all-events")
    public ResponseEntity<Page<Event>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Page<Event> eventsPage = eventService.getAllEvents(PageRequest.of(page, size));
        return ResponseEntity.ok(eventsPage);
    }
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @GetMapping("/category/{category}")
    public List<Event> getEventsByCategory(@PathVariable String category) {
        EventCategory eventCategory = EventCategory.valueOf(category.toUpperCase());
        return eventService.getEventsByCategory(eventCategory);
    }


    @PostMapping("/subscribe")
    public Subscription subscribe(@RequestParam Long eventId, @RequestParam String email) {
        return eventService.subscribe(eventId, email);
    }
    @PutMapping("/event/{id}/rate")
    public ResponseEntity<String> rateEvent(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Event event = eventService.getEvent(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        Integer rating = body.get("rating");
        if (rating != null) {
            event.setRating(rating); // Add rating field to your Event entity
            eventService.updateEvent(event);
            return ResponseEntity.ok("Rating updated successfully");
        }
        return ResponseEntity.badRequest().body("Invalid rating");
    }


}
