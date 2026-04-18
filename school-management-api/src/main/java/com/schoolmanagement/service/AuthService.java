package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.ChangePasswordRequest;
import com.schoolmanagement.dto.request.LoginRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.LoginResponse;
import com.schoolmanagement.entity.Tenant;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.exception.UnauthorizedException;
import com.schoolmanagement.repository.TenantRepository;
import com.schoolmanagement.repository.UserRepository;
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
@Transactional
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {} with school code: {}", request.getEmail(), request.getSchoolCode());

        // Find tenant by slug (school code)
        Tenant tenant = tenantRepository.findBySlug(request.getSchoolCode())
            .orElseThrow(() -> new ResourceNotFoundException("School code not found: " + request.getSchoolCode()));

        if (!tenant.getIsActive()) {
            throw new BadRequestException("School account is inactive");
        }

        // Set tenant context
        TenantContext.setTenantId(tenant.getId());

        // Find user by email and tenant
        User user = userRepository.findByEmailAndTenantId(request.getEmail(), tenant.getId())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new BadRequestException("User account is inactive");
        }
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getEmail());

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
            user.getId(),
            tenant.getId(),
            user.getUserRole().getName(),
            user.getName(),
            user.getEmail()
        );

        // Build response
        LoginResponse.UserDto userDto = LoginResponse.UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getUserRole().getName())
            .tenantId(tenant.getId())
            .build();

        return LoginResponse.builder()
            .token(token)
            .user(userDto)
            .isFirstLogin(user.getIsFirstLogin())
            .build();
    }

    public void changePassword(String userId, ChangePasswordRequest request) {
        log.info("Change password requested for user: {}", userId);

        String tenantId = TenantContext.getTenantId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getTenantId().equals(tenantId)) {
            throw new UnauthorizedException("User does not belong to this tenant");
        }

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setIsFirstLogin(false);
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userId);
    }

    public User getCurrentUser(String userId) {
        String tenantId = TenantContext.getTenantId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getTenantId().equals(tenantId)) {
            throw new UnauthorizedException("User does not belong to this tenant");
        }

        return user;
    }
}