package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.TimetableRequest;
import com.schoolmanagement.dto.response.TimetableResponse;
import com.schoolmanagement.entity.TimetableSlot;
import com.schoolmanagement.entity.DayOfWeek;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.TimetableSlotRepository;
import com.schoolmanagement.repository.DayOfWeekRepository;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimetableService {

    private final TimetableSlotRepository timetableRepository;
    private final DayOfWeekRepository dayOfWeekRepository;

    @Transactional
    public TimetableResponse create(TimetableRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating timetable slot for tenant: {}", tenantId);

        DayOfWeek dayOfWeek = dayOfWeekRepository.findByTenantIdAndCode(tenantId, request.getDayOfWeek())
            .orElseThrow(() -> new ResourceNotFoundException("Day of week not found: " + request.getDayOfWeek()));

        TimetableSlot slot = TimetableSlot.builder()
            .tenantId(tenantId)
            .academicYearId(request.getAcademicYearId())
            .classId(request.getClassId())
            .sectionId(request.getSectionId())
            .dayOfWeek(dayOfWeek)
            .periodNumber(request.getPeriodNumber())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .subjectId(request.getSubjectId())
            .staffId(request.getStaffId())
            .build();

        TimetableSlot saved = timetableRepository.save(slot);
        log.info("Timetable slot created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public TimetableResponse getById(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching timetable slot: {} for tenant: {}", id, tenantId);

        TimetableSlot slot = timetableRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found"));

        if (!slot.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Timetable slot not found");
        }

        return mapToResponse(slot);
    }

    public List<TimetableResponse> getByClassAndSection(String classId, String sectionId) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching timetable for class: {}, section: {}", classId, sectionId);

        List<TimetableSlot> slots = timetableRepository.findByTenantIdAndClassIdAndSectionId(tenantId, classId, sectionId);

        return slots.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<TimetableResponse> getTeacherTimetable(String staffId) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching timetable for staff: {}", staffId);

        List<TimetableSlot> slots = timetableRepository.findTeacherTimetable(tenantId, staffId);

        return slots.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public TimetableResponse update(String id, TimetableRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating timetable slot: {} for tenant: {}", id, tenantId);

        TimetableSlot slot = timetableRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found"));

        if (!slot.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Timetable slot not found");
        }

        DayOfWeek dayOfWeek = dayOfWeekRepository.findByTenantIdAndCode(tenantId, request.getDayOfWeek())
            .orElseThrow(() -> new ResourceNotFoundException("Day of week not found: " + request.getDayOfWeek()));

        slot.setDayOfWeek(dayOfWeek);
        slot.setPeriodNumber(request.getPeriodNumber());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setSubjectId(request.getSubjectId());
        slot.setStaffId(request.getStaffId());
//        slot.setUpdatedAt(LocalDateTime.now());

        TimetableSlot updated = timetableRepository.save(slot);
        log.info("Timetable slot updated: {}", id);

        return mapToResponse(updated);
    }

    @Transactional
    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting timetable slot: {} for tenant: {}", id, tenantId);

        TimetableSlot slot = timetableRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found"));

        if (!slot.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Timetable slot not found");
        }

        timetableRepository.delete(slot);
        log.info("Timetable slot deleted: {}", id);
    }

    private TimetableResponse mapToResponse(TimetableSlot slot) {
        return TimetableResponse.builder()
            .id(slot.getId())
            .classId(slot.getClassId())
            .sectionId(slot.getSectionId())
            .dayOfWeek(slot.getDayOfWeek().getCode())
            .periodNumber(slot.getPeriodNumber())
            .startTime(slot.getStartTime())
            .endTime(slot.getEndTime())
            .subjectId(slot.getSubjectId())
            .staffId(slot.getStaffId())
            .createdAt(slot.getCreatedAt())
            .updatedAt(slot.getUpdatedAt())
            .build();
    }
}
