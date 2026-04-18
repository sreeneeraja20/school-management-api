package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.ChangePasswordRequest;
import com.schoolmanagement.dto.request.LoginRequest;
import com.schoolmanagement.dto.response.LoginResponse;
import com.schoolmanagement.entity.ParentAccount;
import com.schoolmanagement.entity.Tenant;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.exception.UnauthorizedException;
import com.schoolmanagement.repository.ParentAccountRepository;
import com.schoolmanagement.repository.TenantRepository;
import com.schoolmanagement.security.JwtTokenProvider;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentAuthService {

    private final ParentAccountRepository parentAccountRepository;
    private final TenantRepository tenantRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Parent login attempt for user: {} with school code: {}", request.getEmail(), request.getSchoolCode());

        // Find tenant by slug (school code)
        Tenant tenant = tenantRepository.findBySlug(request.getSchoolCode())
            .orElseThrow(() -> new ResourceNotFoundException("School code not found: " + request.getSchoolCode()));

        if (!tenant.getIsActive()) {
            throw new BadRequestException("School account is inactive");
        }

        // Set tenant context
        TenantContext.setTenantId(tenant.getId());

        // Find parent account by email and tenant
        ParentAccount parent = parentAccountRepository.findByEmailAndTenantId(request.getEmail(), tenant.getId())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!parent.getIsActive()) {
            throw new BadRequestException("Parent account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), parent.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Update login tracking
        parent.setLastLoginAt(LocalDateTime.now());
        parent.setLoginCount((parent.getLoginCount() != null ? parent.getLoginCount() : 0) + 1);
        parentAccountRepository.save(parent);

        log.info("Parent logged in successfully: {}", parent.getEmail());

        // Generate JWT token (with longer expiry for parents)
        String token = jwtTokenProvider.generateParentToken(
            parent.getId(),
            tenant.getId(),
            parent.getName(),
            parent.getEmail()
        );

        // Build response
        LoginResponse.UserDto userDto = LoginResponse.UserDto.builder()
            .id(parent.getId())
            .name(parent.getName())
            .email(parent.getEmail())
            .role("PARENT")
            .tenantId(tenant.getId())
            .build();

        return LoginResponse.builder()
            .token(token)
            .user(userDto)
            .isFirstLogin(parent.getIsFirstLogin())
            .build();
    }

    @Transactional
    public void changePassword(String parentId, ChangePasswordRequest request) {
        log.info("Change password requested for parent: {}", parentId);

        String tenantId = TenantContext.getTenantId();

        ParentAccount parent = parentAccountRepository.findById(parentId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent account not found"));

        if (!parent.getTenantId().equals(tenantId)) {
            throw new UnauthorizedException("Parent does not belong to this tenant");
        }

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), parent.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Update password
        parent.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        parent.setIsFirstLogin(false);
        parentAccountRepository.save(parent);

        log.info("Password changed successfully for parent: {}", parentId);
    }

    public ParentAccount getCurrentParent(String parentId) {
        String tenantId = TenantContext.getTenantId();
        ParentAccount parent = parentAccountRepository.findById(parentId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent account not found"));

        if (!parent.getTenantId().equals(tenantId)) {
            throw new UnauthorizedException("Parent does not belong to this tenant");
        }

        return parent;
    }
}
