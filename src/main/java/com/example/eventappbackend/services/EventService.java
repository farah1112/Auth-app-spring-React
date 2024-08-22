package com.example.eventappbackend.services;

import com.example.eventappbackend.DTO.EventDTO;
import com.example.eventappbackend.Repositories.EventRepository;
import com.example.eventappbackend.Repositories.SubscriptionRepository;
import com.example.eventappbackend.entities.Event;
import com.example.eventappbackend.entities.EventCategory;
import com.example.eventappbackend.entities.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> getEvent(Long id) {
        return eventRepository.findById(id);
    }

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getEventsByCategory(EventCategory eventCategory) {
        return eventRepository.findByCategory(eventCategory);
    }
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }


    public String uploadPhoto(MultipartFile photo) throws IOException {
        // Generate a unique file name for the photo
        String fileName = "photo_" + System.currentTimeMillis() + ".jpg";
        // Define the directory within resources/static where photos will be uploaded
        String uploadDir = "src/main/resources/static/";
        // Create the file object with the full path
        File uploadFile = new File(uploadDir + fileName);

        // Ensure that the directory exists
        uploadFile.getParentFile().mkdirs();

        // Write the photo to the file system
        try (FileOutputStream fos = new FileOutputStream(uploadFile)) {
            fos.write(photo.getBytes());
        }

        // Generate the relative URL for the uploaded photo
        String photoUrl = "/" + fileName;

        // Return the URL to be stored in the database
        return photoUrl;
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }
    public List<Event> getEventsByCategoryAndDate(Long currentEventId, EventCategory category, LocalDateTime today) {
        return eventRepository.findEventsByCategoryAndDate(category, today).stream()
                .filter(event -> !event.getId().equals(currentEventId)) // Exclude the current event
                .collect(Collectors.toList());
    }

    public List<Event> getLatestEvents() {
        Pageable topThree = PageRequest.of(0, 3);
        return eventRepository.findLatestEvents(topThree);
    }

}
