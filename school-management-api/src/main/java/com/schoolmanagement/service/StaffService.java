package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.StaffRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.StaffResponse;
import com.schoolmanagement.entity.Staff;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.StaffRepository;
import com.schoolmanagement.security.TenantContext;
import com.schoolmanagement.util.TableQueryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffService {

    private final StaffRepository staffRepository;

    @Transactional
    public StaffResponse create(StaffRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating staff: {} for tenant: {}", request.getName(), tenantId);

        Staff staff = Staff.builder()
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

        Staff staff = staffRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        return mapToResponse(staff);
    }

    public PageResponse<StaffResponse> getAll(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all staff for tenant: {}", tenantId);

        Pageable pageable = TableQueryUtils.buildPageable(request, "name");
        Specification<Staff> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("name", "email", "phone", "designation", "department"),
                List.of("name", "email", "phone", "designation", "department", "isActive")
        );

        Page<Staff> staffPage = staffRepository.findAll(specification, pageable);

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

    @Transactional
    public StaffResponse update(String id, StaffRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating staff: {} for tenant: {}", id, tenantId);

        Staff staff = staffRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

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

    @Transactional
    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting staff: {} for tenant: {}", id, tenantId);

        Staff staff = staffRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

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
