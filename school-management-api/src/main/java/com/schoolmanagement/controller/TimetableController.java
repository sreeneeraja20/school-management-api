package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.TimetableRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.TimetableResponse;
import com.schoolmanagement.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@Tag(name = "Timetable", description = "Timetable management endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create timetable slot", description = "Create a new timetable slot")
    public ResponseEntity<ApiResponse<TimetableResponse>> createTimetable(@Valid @RequestBody TimetableRequest request) {
        log.info("Creating timetable slot");
        TimetableResponse response = timetableService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Timetable slot created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get timetable slot by ID", description = "Get details of a specific timetable slot")
    public ResponseEntity<ApiResponse<TimetableResponse>> getTimetable(@PathVariable String id) {
        log.info("Fetching timetable slot: {}", id);
        TimetableResponse response = timetableService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Timetable slot retrieved successfully", response));
    }

    @GetMapping("/class/{classId}/section/{sectionId}")
    @Operation(summary = "Get class timetable", description = "Get timetable for a class and section")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getClassTimetable(
            @PathVariable String classId,
            @PathVariable String sectionId) {
        log.info("Fetching timetable for class: {}, section: {}", classId, sectionId);
        List<TimetableResponse> response = timetableService.getByClassAndSection(classId, sectionId);
        return ResponseEntity.ok(ApiResponse.success("Timetable retrieved successfully", response));
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Get teacher timetable", description = "Get timetable for a specific teacher")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTeacherTimetable(
            @PathVariable String staffId) {
        log.info("Fetching timetable for staff: {}", staffId);
        List<TimetableResponse> response = timetableService.getTeacherTimetable(staffId);
        return ResponseEntity.ok(ApiResponse.success("Timetable retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update timetable slot", description = "Update timetable slot details")
    public ResponseEntity<ApiResponse<TimetableResponse>> updateTimetable(
            @PathVariable String id,
            @Valid @RequestBody TimetableRequest request) {
        log.info("Updating timetable slot: {}", id);
        TimetableResponse response = timetableService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Timetable slot updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete timetable slot", description = "Delete a timetable slot")
    public ResponseEntity<ApiResponse<String>> deleteTimetable(@PathVariable String id) {
        log.info("Deleting timetable slot: {}", id);
        timetableService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Timetable slot deleted successfully", null));
    }
}