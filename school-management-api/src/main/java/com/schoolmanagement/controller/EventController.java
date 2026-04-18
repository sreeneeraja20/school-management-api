package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.EventRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.EventResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create event", description = "Create a new event")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("Creating event: {}", request.getTitle());
        EventResponse response = eventService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Event created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Get details of a specific event")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(@PathVariable String id) {
        log.info("Fetching event: {}", id);
        EventResponse response = eventService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all events", description = "Get paginated list of events")
    public ResponseEntity<ApiResponse<PageResponse<EventResponse>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching all events - page: {}, size: {}", page, size);
        PageResponse<EventResponse> response = eventService.getAll(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update event", description = "Update event details")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable String id,
            @Valid @RequestBody EventRequest request) {
        log.info("Updating event: {}", id);
        EventResponse response = eventService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete event", description = "Delete an event")
    public ResponseEntity<ApiResponse<String>> deleteEvent(@PathVariable String id) {
        log.info("Deleting event: {}", id);
        eventService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
    }

    @GetMapping("/search")
    @Operation(summary = "Search events", description = "Search events by title or description")
    public ResponseEntity<ApiResponse<PageResponse<EventResponse>>> searchEvents(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching events: {}", search);
        PageResponse<EventResponse> response = eventService.searchEvents(search, page, size);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", response));
    }
}