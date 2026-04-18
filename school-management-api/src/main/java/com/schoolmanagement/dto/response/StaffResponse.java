package com.schoolmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private String id;

    private String name;

    private String email;

    private String phone;

    private String designation;

    private String department;

    private String qualification;

    private LocalDate dateOfJoining;

    private String gender;

    private String address;

    private String photoUrl;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}