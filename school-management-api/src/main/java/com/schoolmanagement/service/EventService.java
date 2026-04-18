package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.EventRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.EventResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.entity.Event;
import com.schoolmanagement.entity.EventType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.EventRepository;
import com.schoolmanagement.repository.EventTypeRepository;
import com.schoolmanagement.security.TenantContext;
import com.schoolmanagement.util.TableQueryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;

    @Transactional
    public EventResponse create(EventRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating event: {} for tenant: {}", request.getTitle(), tenantId);

        EventType eventType = eventTypeRepository.findByTenantIdAndName(tenantId, request.getType())
            .orElseThrow(() -> new ResourceNotFoundException("Event type not found: " + request.getType()));

        Event event = Event.builder()
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

        Event event = eventRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return mapToResponse(event);
    }

    public PageResponse<EventResponse> getAll(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all events for tenant: {}", tenantId);

        Pageable pageable = TableQueryUtils.buildPageable(request, "startDate");
        Specification<Event> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("title", "description", "eventType.name"),
                List.of("title", "description", "isActive", "eventType.name")
        );

        Page<Event> eventPage = eventRepository.findAll(specification, pageable);

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

    @Transactional
    public EventResponse update(String id, EventRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating event: {} for tenant: {}", id, tenantId);

        Event event = eventRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

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

    @Transactional
    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting event: {} for tenant: {}", id, tenantId);

        Event event = eventRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        eventRepository.delete(event);
        log.info("Event deleted: {}", id);
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
