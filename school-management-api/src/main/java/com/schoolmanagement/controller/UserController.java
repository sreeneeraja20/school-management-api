package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.UserRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.UserResponse;
import com.schoolmanagement.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User account management endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        log.info("Creating user: {}", request.getEmail());
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get details of a specific user")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String id) {
        log.info("Fetching user: {}", id);
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @PostMapping("/search")
    @Operation(summary = "Search users", description = "Search/filter/sort users with pagination")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(
            @RequestBody(required = false) TablePageRequest request) {
        TablePageRequest pageRequest = request == null ? new TablePageRequest() : request;
        log.info("Searching users - page: {}, size: {}", pageRequest.getPage(), pageRequest.getSize());
        PageResponse<UserResponse> response = userService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user details")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequest request) {
        log.info("Updating user: {}", id);
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user account")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        log.info("Deleting user: {}", id);
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Reset password", description = "Reset user password to temporary password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable String id) {
        log.info("Resetting password for user: {}", id);
        userService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Toggle user active status", description = "Activate or deactivate user account")
    public ResponseEntity<ApiResponse<String>> toggleActive(@PathVariable String id) {
        log.info("Toggling active status for user: {}", id);
        userService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", null));
    }
}
