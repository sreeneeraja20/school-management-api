package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.LoginRequest;
import com.schoolmanagement.dto.request.ChangePasswordRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.LoginResponse;
import com.schoolmanagement.service.ParentAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/parent-auth")
@RequiredArgsConstructor
@Tag(name = "Parent Authentication", description = "Authentication endpoints for parents")
public class ParentAuthController {

    private final ParentAuthService parentAuthService;

    @PostMapping("/login")
    @Operation(summary = "Parent login", description = "Login parent account with school code, email and password")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Parent login request for user: {}", request.getEmail());
        LoginResponse response = parentAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('PARENT')")
    @Operation(summary = "Change parent password", description = "Change password for authenticated parent")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("Parent change password request");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String parentId = auth.getName();
        
        parentAuthService.changePassword(parentId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PARENT')")
    @Operation(summary = "Get current parent", description = "Get details of authenticated parent")
    public ResponseEntity<ApiResponse<Object>> getCurrentParent() {
        log.info("Get current parent request");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success("Parent details", auth.getPrincipal()));
    }
}