package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.EventRequest;
import com.schoolmanagement.dto.response.EventResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.entity.Event;
import com.schoolmanagement.entity.EventType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.EventRepository;
import com.schoolmanagement.repository.EventTypeRepository;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;

    public EventResponse create(EventRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating event: {} for tenant: {}", request.getTitle(), tenantId);

        EventType eventType = eventTypeRepository.findByTenantIdAndName(tenantId, request.getType())
            .orElseThrow(() -> new ResourceNotFoundException("Event type not found: " + request.getType()));

        Event event = Event.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .title(request.getTitle())
            .description(request.getDescription())
            .eventType(eventType)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .isActive(true)
            .build();

        Event saved = eventRepository.save(event);
        log.info("Event created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public EventResponse getById(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching event: {} for tenant: {}", id, tenantId);

        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        return mapToResponse(event);
    }

    public PageResponse<EventResponse> getAll(int page, int size, String sortBy, String sortDir) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all events for tenant: {}", tenantId);

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Event> eventPage = eventRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<EventResponse>builder()
            .content(eventPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()))
            .page(eventPage.getNumber())
            .size(eventPage.getSize())
            .totalElements(eventPage.getTotalElements())
            .totalPages(eventPage.getTotalPages())
            .last(eventPage.isLast())
            .first(eventPage.isFirst())
            .build();
    }

    public EventResponse update(String id, EventRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating event: {} for tenant: {}", id, tenantId);

        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        EventType eventType = eventTypeRepository.findByTenantIdAndName(tenantId, request.getType())
            .orElseThrow(() -> new ResourceNotFoundException("Event type not found: " + request.getType()));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventType(eventType);
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
//        event.setUpdatedAt(LocalDateTime.now());

        Event updated = eventRepository.save(event);
        log.info("Event updated: {}", id);

        return mapToResponse(updated);
    }

    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting event: {} for tenant: {}", id, tenantId);

        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        eventRepository.delete(event);
        log.info("Event deleted: {}", id);
    }

    public PageResponse<EventResponse> searchEvents(String search, int page, int size) {
        String tenantId = TenantContext.getTenantId();
        log.info("Searching events: {} for tenant: {}", search, tenantId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.searchEvents(tenantId, search, pageable);

        return PageResponse.<EventResponse>builder()
            .content(eventPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()))
            .page(eventPage.getNumber())
            .size(eventPage.getSize())
            .totalElements(eventPage.getTotalElements())
            .totalPages(eventPage.getTotalPages())
            .last(eventPage.isLast())
            .first(eventPage.isFirst())
            .build();
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
            .id(event.getId())
            .title(event.getTitle())
            .description(event.getDescription())
            .type(event.getEventType().getName())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .isActive(event.getIsActive())
            .createdAt(event.getCreatedAt())
            .updatedAt(event.getUpdatedAt())
            .build();
    }
}