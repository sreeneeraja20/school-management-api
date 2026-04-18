package com.schoolmanagement.controller;

import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.entity.Tenant;
import com.schoolmanagement.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant/School management endpoints")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping("/by-slug/{slug}")
    @Operation(summary = "Get tenant by slug", description = "Get tenant details by school code (public endpoint)")
    public ResponseEntity<ApiResponse<Tenant>> getTenantBySlug(@PathVariable String slug) {
        log.info("Fetching tenant by slug: {}", slug);
        Tenant tenant = tenantService.getTenantBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", tenant));
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current tenant", description = "Get current tenant details for authenticated user")
    public ResponseEntity<ApiResponse<Tenant>> getCurrentTenant() {
        log.info("Fetching current tenant");
        Tenant tenant = tenantService.getCurrentTenant();
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", tenant));
    }
}