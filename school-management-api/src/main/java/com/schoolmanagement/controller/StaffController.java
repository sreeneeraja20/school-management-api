package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.StaffRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.StaffResponse;
import com.schoolmanagement.service.StaffService;
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
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Staff", description = "Staff management endpoints")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create staff", description = "Create a new staff member")
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(@Valid @RequestBody StaffRequest request) {
        log.info("Creating staff: {}", request.getName());
        StaffResponse response = staffService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Staff created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get staff by ID", description = "Get details of a specific staff member")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaff(@PathVariable String id) {
        log.info("Fetching staff: {}", id);
        StaffResponse response = staffService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get all staff", description = "Get paginated list of staff members")
    public ResponseEntity<ApiResponse<PageResponse<StaffResponse>>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Fetching all staff - page: {}, size: {}", page, size);
        PageResponse<StaffResponse> response = staffService.getAll(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update staff", description = "Update staff details")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaff(
            @PathVariable String id,
            @Valid @RequestBody StaffRequest request) {
        log.info("Updating staff: {}", id);
        StaffResponse response = staffService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Staff updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete staff", description = "Delete a staff member")
    public ResponseEntity<ApiResponse<String>> deleteStaff(@PathVariable String id) {
        log.info("Deleting staff: {}", id);
        staffService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Staff deleted successfully", null));
    }
}