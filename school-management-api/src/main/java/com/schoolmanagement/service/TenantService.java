package com.schoolmanagement.service;

import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.entity.Tenant;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.TenantRepository;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantService {

    private final TenantRepository tenantRepository;

    public Tenant getTenantBySlug(String slug) {
        log.info("Fetching tenant by slug: {}", slug);
        return tenantRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("School not found: " + slug));
    }

    public Tenant getCurrentTenant() {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching current tenant: {}", tenantId);
        return tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    public Tenant getTenantById(String tenantId) {
        return tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));
    }
}