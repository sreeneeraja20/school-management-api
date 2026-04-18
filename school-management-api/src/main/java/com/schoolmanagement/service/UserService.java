package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.UserRequest;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.UserResponse;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.entity.UserRole;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.DuplicateResourceException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.UserRepository;
import com.schoolmanagement.repository.UserRoleRepository;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating user: {} for tenant: {}", request.getEmail(), tenantId);

        // Check if user already exists
        if (userRepository.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new DuplicateResourceException("User with email already exists: " + request.getEmail());
        }

        // Find user role
        UserRole userRole = userRoleRepository.findByTenantIdAndName(tenantId, request.getRole())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));

        // Generate temporary password
        String tempPassword = generateTemporaryPassword();

        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(tempPassword))
            .userRole(userRole)
            .name(request.getName())
            .staffId(request.getStaffId())
            .isFirstLogin(true)
            .isActive(true)
            .build();

        User saved = userRepository.save(user);
        log.info("User created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public UserResponse getById(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching user: {} for tenant: {}", id, tenantId);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return mapToResponse(user);
    }

    public PageResponse<UserResponse> getAll(int page, int size, String sortBy, String sortDir) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all users for tenant: {}", tenantId);

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> userPage = userRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<UserResponse>builder()
            .content(userPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()))
            .page(userPage.getNumber())
            .size(userPage.getSize())
            .totalElements(userPage.getTotalElements())
            .totalPages(userPage.getTotalPages())
            .last(userPage.isLast())
            .first(userPage.isFirst())
            .build();
    }

    public UserResponse update(String id, UserRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating user: {} for tenant: {}", id, tenantId);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("User not found");
        }

        user.setName(request.getName());
        user.setStaffId(request.getStaffId());

        // Update role if provided
        if (request.getRole() != null) {
            UserRole userRole = userRoleRepository.findByTenantIdAndName(tenantId, request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));
            user.setUserRole(userRole);
        }

        User updated = userRepository.save(user);
        log.info("User updated: {}", id);

        return mapToResponse(updated);
    }

    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting user: {} for tenant: {}", id, tenantId);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("User not found");
        }

        // Prevent deleting last admin
        if ("ADMIN".equals(user.getUserRole().getName()) && user.getIsActive()) {
            long activeAdminCount = userRepository.countActiveAdmins(tenantId);
            if (activeAdminCount <= 1) {
                throw new BadRequestException("Cannot delete the last active admin");
            }
        }

        userRepository.delete(user);
        log.info("User deleted: {}", id);
    }

    public void resetPassword(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Resetting password for user: {} for tenant: {}", id, tenantId);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("User not found");
        }

        String newPassword = generateTemporaryPassword();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setIsFirstLogin(true);
        userRepository.save(user);

        log.info("Password reset for user: {}", id);
    }

    public void toggleActive(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Toggling active status for user: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("User not found");
        }

        // Prevent deactivating last admin
        if ("ADMIN".equals(user.getUserRole().getName()) && user.getIsActive()) {
            long activeAdminCount = userRepository.countActiveAdmins(tenantId);
            if (activeAdminCount <= 1) {
                throw new BadRequestException("Cannot deactivate the last active admin");
            }
        }

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        log.info("User active status toggled: {}", id);
    }

    private String generateTemporaryPassword() {
        // Generate a random temporary password
        return "TempPass@" + System.currentTimeMillis() % 10000;
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getUserRole().getName())
            .staffId(user.getStaffId())
            .isFirstLogin(user.getIsFirstLogin())
            .isActive(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}