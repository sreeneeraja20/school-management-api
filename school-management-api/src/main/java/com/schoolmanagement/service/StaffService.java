package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.StaffRequest;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.StaffResponse;
import com.schoolmanagement.entity.Staff;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.StaffRepository;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffResponse create(StaffRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating staff: {} for tenant: {}", request.getName(), tenantId);

        Staff staff = Staff.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .designation(request.getDesignation())
            .department(request.getDepartment())
            .qualification(request.getQualification())
            .dateOfJoining(request.getDateOfJoining())
            .gender(request.getGender())
            .address(request.getAddress())
            .photoUrl(request.getPhotoUrl())
            .isActive(true)
            .build();

        Staff saved = staffRepository.save(staff);
        log.info("Staff created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public StaffResponse getById(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching staff: {} for tenant: {}", id, tenantId);

        Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        if (!staff.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Staff not found");
        }

        return mapToResponse(staff);
    }

    public PageResponse<StaffResponse> getAll(int page, int size, String sortBy, String sortDir) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all staff for tenant: {}", tenantId);

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Staff> staffPage = staffRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<StaffResponse>builder()
            .content(staffPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()))
            .page(staffPage.getNumber())
            .size(staffPage.getSize())
            .totalElements(staffPage.getTotalElements())
            .totalPages(staffPage.getTotalPages())
            .last(staffPage.isLast())
            .first(staffPage.isFirst())
            .build();
    }

    public StaffResponse update(String id, StaffRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating staff: {} for tenant: {}", id, tenantId);

        Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        if (!staff.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Staff not found");
        }

        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setDesignation(request.getDesignation());
        staff.setDepartment(request.getDepartment());
        staff.setQualification(request.getQualification());
        staff.setDateOfJoining(request.getDateOfJoining());
        staff.setGender(request.getGender());
        staff.setAddress(request.getAddress());
        staff.setPhotoUrl(request.getPhotoUrl());

        Staff updated = staffRepository.save(staff);
        log.info("Staff updated: {}", id);

        return mapToResponse(updated);
    }

    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting staff: {} for tenant: {}", id, tenantId);

        Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        if (!staff.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Staff not found");
        }

        staffRepository.delete(staff);
        log.info("Staff deleted: {}", id);
    }

    private StaffResponse mapToResponse(Staff staff) {
        return StaffResponse.builder()
            .id(staff.getId())
            .name(staff.getName())
            .email(staff.getEmail())
            .phone(staff.getPhone())
            .designation(staff.getDesignation())
            .department(staff.getDepartment())
            .qualification(staff.getQualification())
            .dateOfJoining(staff.getDateOfJoining())
            .gender(staff.getGender())
            .address(staff.getAddress())
            .photoUrl(staff.getPhotoUrl())
            .isActive(staff.getIsActive())
            .createdAt(staff.getCreatedAt())
            .updatedAt(staff.getUpdatedAt())
            .build();
    }
}